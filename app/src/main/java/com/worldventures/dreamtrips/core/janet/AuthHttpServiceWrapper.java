package com.worldventures.dreamtrips.core.janet;

import android.content.Context;

import com.techery.spares.module.Injector;
import com.techery.spares.session.SessionHolder;
import com.worldventures.dreamtrips.core.api.action.BaseHttpAction;
import com.worldventures.dreamtrips.core.session.UserSession;
import com.worldventures.dreamtrips.core.utils.AppVersionNameBuilder;
import com.worldventures.dreamtrips.core.utils.LocaleHelper;

import javax.inject.Inject;

import io.techery.janet.ActionHolder;
import io.techery.janet.ActionService;
import io.techery.janet.ActionServiceWrapper;
import io.techery.janet.JanetException;

public class AuthHttpServiceWrapper extends ActionServiceWrapper {
    @Inject SessionHolder<UserSession> appSessionHolder;
    @Inject LocaleHelper localeHelper;
    @Inject AppVersionNameBuilder appVersionNameBuilder;

    public AuthHttpServiceWrapper(ActionService actionService, Context appContext) {
        super(actionService);
        ((Injector) appContext).inject(this);
    }

    @Override
    protected <A> boolean onInterceptSend(ActionHolder<A> holder) {
        A action = holder.action();
        if (!(action instanceof BaseHttpAction)) return false;
        BaseHttpAction baseHttpAction = (BaseHttpAction) action;
        if (appSessionHolder.get().isPresent()) {
            UserSession userSession = appSessionHolder.get().get();
            baseHttpAction.setAuthorizationHeader("Token token=" + userSession.getApiToken());
        }
        baseHttpAction.setAppVersionHeader(appVersionNameBuilder.getSemanticVersionName());
        baseHttpAction.setLanguageHeader(localeHelper.getDefaultLocaleFormatted());
        return false;
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
    }

    @Override
    protected <A> void onInterceptFail(ActionHolder<A> holder, JanetException e) {
    }
}
