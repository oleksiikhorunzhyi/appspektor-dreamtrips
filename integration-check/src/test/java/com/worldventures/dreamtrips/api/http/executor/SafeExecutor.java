package com.worldventures.dreamtrips.api.http.executor;

import com.worldventures.dreamtrips.api.api_common.BaseHttpAction;

import org.jetbrains.annotations.Nullable;

public class SafeExecutor<T extends ActionExecutor> implements ActionExecutor {

    private final T worker;

    public static <T extends ActionExecutor> SafeExecutor<T> from(T executor) {
        return new SafeExecutor<T>(executor);
    }

    private SafeExecutor(T worker) {
        this.worker = worker;
    }

    @Override
    @Nullable
    public <T extends BaseHttpAction> T execute(T action) {
        try { return worker.execute(action); } catch (Throwable e) { return null; }
    }
}
