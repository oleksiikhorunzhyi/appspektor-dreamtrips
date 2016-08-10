package com.worldventures.dreamtrips.core.janet;

import android.content.Context;
import android.support.annotation.Nullable;

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
import com.worldventures.dreamtrips.modules.common.model.Session;

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
        prepareHttpAction((BaseHttpAction) action);
        return false;
    }

    private void prepareHttpAction(BaseHttpAction action) {
        action.setAppVersionHeader(appVersionNameBuilder.getSemanticVersionName());
        action.setLanguageHeader(localeHelper.getDefaultLocaleFormatted());
        if (action instanceof AuthorizedHttpAction
                && appSessionHolder.get().isPresent()) {
            UserSession userSession = appSessionHolder.get().get();
            ((AuthorizedHttpAction) action).setAuthorizationHeader("Token token=" + userSession.getApiToken());
        }
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

        //checking with retry-login policy
        if ((holder.action() instanceof AuthorizedHttpAction)
                && !(holder.action() instanceof LogoutAction)
                && !retriedActions.remove(holder.action())) {
            AuthorizedHttpAction action = (AuthorizedHttpAction) holder.action();
            synchronized (this) {
                if (!action.getAuthorizationHeader()
                        .endsWith(appSessionHolder.get().get().getApiToken())) {
                    prepareHttpAction(action);
                    Timber.d("Action %s will be sent again because of invalid token", action);
                    return true;
                }
                boolean shouldRetry = retryPolicy.handle(e, () -> createSession(action));
                if (shouldRetry) {
                    Timber.d("Action %s will be sent again after relogining", action);
                    prepareHttpAction(action);
                    retriedActions.add(action);
                }
                return shouldRetry;
            }
        }
        return false;
    }

    @Nullable
    private Session createSession(AuthorizedHttpAction action) {
        UserSession userSession = appSessionHolder.get().get();
        String username = userSession.getUsername();
        String userPassword = userSession.getUserPassword();
        LoginAction loginAction = new LoginAction(username, userPassword);
        prepareHttpAction(loginAction);
        Timber.d("Relogin for %s", action);
        ActionState<LoginAction> loginState = loginActionPipe.createObservable(loginAction)
                .toBlocking()
                .last();
        if (loginState.status == ActionState.Status.SUCCESS) {
            return loginState.action.getLoginResponse();
        } else {
            Timber.w(loginState.exception, "Login error");
        }
        return null;
    }
}