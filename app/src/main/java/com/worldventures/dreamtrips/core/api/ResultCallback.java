package com.worldventures.dreamtrips.core.api;

import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

public class ResultCallback<T> implements Callback<T> {

    public static interface Result<T> {
        void response(T t, Exception e);
    }

    private final Result<T> resultHandler;

    public ResultCallback(Result<T> resultHandler) {
        this.resultHandler = resultHandler;
    }

    @Override
    public void success(T t, Response response) {
        this.resultHandler.response(t, null);
    }

    @Override
    public void failure(RetrofitError error) {
        this.resultHandler.response(null, error);
    }
}
