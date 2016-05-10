package com.messenger.util.janet;

import io.techery.janet.ActionHolder;
import io.techery.janet.ActionServiceWrapper;
import io.techery.janet.CommandActionService;
import io.techery.janet.JanetException;

public class BaseCommandActionServiceWrapper extends ActionServiceWrapper {

    public BaseCommandActionServiceWrapper() {
        super(new CommandActionService());
    }

    @Override
    protected <A> boolean onInterceptSend(ActionHolder<A> holder) throws JanetException {
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
