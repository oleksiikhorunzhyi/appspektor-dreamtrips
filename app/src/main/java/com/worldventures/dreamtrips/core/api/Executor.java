package com.worldventures.dreamtrips.core.api;

import retrofit.Callback;

public interface Executor<T> {
        void execute(Callback<T> callback);
    }
