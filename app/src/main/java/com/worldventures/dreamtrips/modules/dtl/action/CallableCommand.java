package com.worldventures.dreamtrips.modules.dtl.action;

import java.util.concurrent.Callable;

import io.techery.janet.CommandActionBase;

public class CallableCommand<T> extends CommandActionBase<T> {

    private final Callable<T> callable;

    public CallableCommand(Callable<T> callable) {
        this.callable = callable;
    }

    @Override
    protected final void run(CommandCallback<T> callback) throws Throwable {
        callback.onSuccess(callable.call());
    }
}
