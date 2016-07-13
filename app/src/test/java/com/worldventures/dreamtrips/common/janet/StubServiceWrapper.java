package com.worldventures.dreamtrips.common.janet;

import io.techery.janet.ActionHolder;
import io.techery.janet.ActionService;
import io.techery.janet.ActionServiceWrapper;
import io.techery.janet.JanetException;

public class StubServiceWrapper extends ActionServiceWrapper {

    private Callback callback;

    public StubServiceWrapper(ActionService actionService) {
        super(actionService);
    }

    public void setCallback(Callback callback) {
        this.callback = callback;
    }

    public Callback getCallback() {
        return callback;
    }

    @Override protected <A> boolean onInterceptSend(ActionHolder<A> holder) {
        if (callback != null) {
            callback.onSend(holder);
        }
        return false;
    }

    @Override protected <A> void onInterceptCancel(ActionHolder<A> holder) {
        if (callback != null) {
            callback.onCancel(holder);
        }
    }

    @Override protected <A> void onInterceptStart(ActionHolder<A> holder) {
        if (callback != null) {
            callback.onStart(holder);
        }
    }

    @Override protected <A> void onInterceptProgress(ActionHolder<A> holder, int progress) {
        if (callback != null) {
            callback.onProgress(holder, progress);
        }
    }

    @Override protected <A> void onInterceptSuccess(ActionHolder<A> holder) {
        if (callback != null) {
            callback.onSuccess(holder);
        }
    }

    @Override protected <A> boolean onInterceptFail(ActionHolder<A> holder, JanetException e) {
        if (callback != null) {
            callback.onFail(holder, e);
        }
        return false;
    }

    public interface Callback {
        <A> void onSend(ActionHolder<A> holder);

        <A> void onCancel(ActionHolder<A> holder);

        <A> void onStart(ActionHolder<A> holder);

        <A> void onProgress(ActionHolder<A> holder, int progress);

        <A> void onSuccess(ActionHolder<A> holder);

        <A> void onFail(ActionHolder<A> holder, JanetException e);
    }
}
