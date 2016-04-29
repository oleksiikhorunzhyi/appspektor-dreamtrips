package com.worldventures.dreamtrips.core.api.action;

import java.util.concurrent.Callable;

import io.techery.janet.CommandActionBase;

public class CallableCommandAction<T> extends CommandActionBase<T> {

    private final Callable<T> callable;

    public CallableCommandAction(Callable<T> callable) {
        this.callable = callable;
    }

    @Override
    protected final void run(CommandCallback<T> callback) throws Throwable {
        callback.onSuccess(callable.call());
    }
}
