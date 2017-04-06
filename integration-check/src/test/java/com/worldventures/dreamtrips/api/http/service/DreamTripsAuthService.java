package com.worldventures.dreamtrips.api.http.service;

import com.worldventures.dreamtrips.api.api_common.AuthorizedHttpAction;
import com.worldventures.dreamtrips.api.session.LoginHttpAction;
import com.worldventures.dreamtrips.api.session.model.Session;

import io.techery.janet.ActionHolder;
import io.techery.janet.ActionService;
import io.techery.janet.ActionServiceWrapper;
import io.techery.janet.JanetException;

public class DreamTripsAuthService extends ActionServiceWrapper {

    public DreamTripsAuthService(ActionService actionService) {
        super(actionService);
    }

    private Session session;

    public Session getSession() {
        return session;
    }

    @Override
    protected <A> boolean onInterceptSend(ActionHolder<A> holder) throws JanetException {
        if (holder.action() instanceof AuthorizedHttpAction) {
            tryAuthorizeAction((AuthorizedHttpAction) holder.action());
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
        if (holder.action() instanceof LoginHttpAction) {
            session = ((LoginHttpAction) holder.action()).response();
        }
    }

    @Override
    protected <A> boolean onInterceptFail(ActionHolder<A> holder, JanetException e) {
        return false;
    }

    private void tryAuthorizeAction(AuthorizedHttpAction action) {
        if (session == null) return;
        action.setAuthorizationHeader("Token token=" + session.token());
    }
}
