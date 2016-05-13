package com.worldventures.dreamtrips.core.janet;

import android.content.Context;

import com.techery.spares.module.Injector;
import com.techery.spares.session.SessionHolder;
import com.worldventures.dreamtrips.core.api.AuthRetryPolicy;
import com.worldventures.dreamtrips.core.api.action.AuthorizedHttpAction;
import com.worldventures.dreamtrips.core.api.action.BaseHttpAction;
import com.worldventures.dreamtrips.core.api.action.LoginAction;
import com.worldventures.dreamtrips.core.api.action.LogoutAction;
import com.worldventures.dreamtrips.core.session.UserSession;
import com.worldventures.dreamtrips.core.utils.AppVersionNameBuilder;
import com.worldventures.dreamtrips.core.utils.LocaleHelper;

import java.util.Set;
import java.util.concurrent.CopyOnWriteArraySet;

import javax.inject.Inject;

import io.techery.janet.ActionHolder;
import io.techery.janet.ActionPipe;
import io.techery.janet.ActionServiceWrapper;
import io.techery.janet.ActionState;
import io.techery.janet.HttpActionService;
import io.techery.janet.Janet;
import io.techery.janet.JanetException;
import io.techery.janet.converter.Converter;
import io.techery.janet.http.HttpClient;
import timber.log.Timber;

public class DreamTripsHttpService extends ActionServiceWrapper {

    @Inject
    SessionHolder<UserSession> appSessionHolder;
    @Inject
    LocaleHelper localeHelper;
    @Inject
    AppVersionNameBuilder appVersionNameBuilder;

    private final ActionPipe<LoginAction> loginActionPipe;
    private final Set<Object> retriedActions = new CopyOnWriteArraySet<>();
    private final AuthRetryPolicy retryPolicy;

    public DreamTripsHttpService(Context appContext, String baseUrl, HttpClient client, Converter converter) {
        super(new HttpActionService(baseUrl, client, converter));
        ((Injector) appContext).inject(this);
        loginActionPipe = new Janet.Builder()
                .addService(new HttpActionService(baseUrl, client, converter))
                .build()
                .createPipe(LoginAction.class);
        retryPolicy = new AuthRetryPolicy(appSessionHolder);
    }

    @Override
    protected <A> boolean onInterceptSend(ActionHolder<A> holder) {
        A action = holder.action();
        if (!(action instanceof BaseHttpAction)) return false;
        BaseHttpAction baseHttpAction = (BaseHttpAction) action;
        fillBaseData(baseHttpAction);
        if (action instanceof AuthorizedHttpAction
                && appSessionHolder.get().isPresent()) {
            UserSession userSession = appSessionHolder.get().get();
            ((AuthorizedHttpAction) baseHttpAction).setAuthorizationHeader("Token token=" + userSession.getApiToken());
        }
        return false;
    }

    private void fillBaseData(BaseHttpAction baseHttpAction) {
        baseHttpAction.setAppVersionHeader(appVersionNameBuilder.getSemanticVersionName());
        baseHttpAction.setLanguageHeader(localeHelper.getDefaultLocaleFormatted());
    }

    @Override
    protected <A> void onInterceptCancel(ActionHolder<A> holder) {
    }

    @Override
    protected <A> void onInterceptStart(ActionHolder<A> holder) {
    }

    @Override
    protected <A> void onInterceptProgress(ActionHolder<A> holder, int progress) {
    }

    @Override
    protected <A> void onInterceptSuccess(ActionHolder<A> holder) {
        retriedActions.remove(holder.action());
    }

    @Override
    protected <A> boolean onInterceptFail(ActionHolder<A> holder, JanetException e) {
        A action = holder.action();

        //checking with retry-login policy
        if ((action instanceof AuthorizedHttpAction)
                && !(action instanceof LogoutAction)
                && !retriedActions.remove(action)) {
            boolean shouldRetry = retryPolicy.handle(e, () -> {
                UserSession userSession = appSessionHolder.get().get();
                String username = userSession.getUsername();
                String userPassword = userSession.getUserPassword();
                LoginAction loginAction = new LoginAction(username, userPassword);
                fillBaseData(loginAction);
                ActionState<LoginAction> loginState = loginActionPipe.createObservable(loginAction)
                        .toBlocking()
                        .last();
                if (loginState.status == ActionState.Status.SUCCESS) {
                    return loginState.action.getLoginResponse();
                } else {
                    Timber.w(loginState.exception, "Login error");
                }
                return null;
            });
            if (shouldRetry) {
                Timber.d("Action %s will be sent again", action);
                retriedActions.add(action);
            }
            return shouldRetry;
        }
        return false;
    }
}
