package com.worldventures.dreamtrips.core.api;

import com.worldventures.dreamtrips.core.session.AppSessionHolder;

import org.apache.http.HttpStatus;

import retrofit.ErrorHandler;
import retrofit.RetrofitError;

public class DefaultErrorHandler implements ErrorHandler {
    private final AppSessionHolder appSessionHolder;

    public DefaultErrorHandler(AppSessionHolder appSessionHolder) {
        this.appSessionHolder = appSessionHolder;
    }

    @Override
    public Throwable handleError(RetrofitError cause) {

        if (cause.getResponse().getStatus() == HttpStatus.SC_UNAUTHORIZED) {
            this.appSessionHolder.destroy();
        }

        return cause;
    }
}
