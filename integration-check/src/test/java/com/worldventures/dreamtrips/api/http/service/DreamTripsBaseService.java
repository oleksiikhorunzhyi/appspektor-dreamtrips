package com.worldventures.dreamtrips.api.http.service;

import com.worldventures.dreamtrips.api.api_common.BaseHttpAction;
import com.worldventures.dreamtrips.api.http.EnvParams;

import io.techery.janet.ActionHolder;
import io.techery.janet.ActionService;
import io.techery.janet.ActionServiceWrapper;
import io.techery.janet.JanetException;

public class DreamTripsBaseService extends ActionServiceWrapper {

    private final EnvParams env;

    public DreamTripsBaseService(ActionService actionService, EnvParams env) {
        super(actionService);
        this.env = env;
    }

    @Override
    protected <A> boolean onInterceptSend(ActionHolder<A> holder) throws JanetException {
        if (holder.action() instanceof BaseHttpAction) {
            prepareHttpAction((BaseHttpAction) holder.action());
        }
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
    protected <A> boolean onInterceptFail(ActionHolder<A> holder, JanetException e) {
        return false;
    }

    private void prepareHttpAction(BaseHttpAction action) {
        if (action.getAcceptHeader() == null) action.setApiVersionForAccept(env.apiVersion());
        if (action.getAppPlatformHeader() == null) action.setAppPlatformHeader(env.appPlatform());
        if (action.getAppVersionHeader() == null) action.setAppVersionHeader(env.appVersion());
        if (action.getAppLanguageHeader() == null) action.setAppLanguageHeader(env.appLanguage());
    }
}
