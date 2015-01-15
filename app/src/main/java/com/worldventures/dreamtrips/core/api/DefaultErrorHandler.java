package com.worldventures.dreamtrips.core.api;

import com.worldventures.dreamtrips.core.SessionManager;

import org.apache.http.HttpStatus;

import retrofit.ErrorHandler;
import retrofit.RetrofitError;

public class DefaultErrorHandler implements ErrorHandler {
    private final SessionManager sessionManager;

    public DefaultErrorHandler(SessionManager sessionManager) {
        this.sessionManager = sessionManager;
    }

    @Override
    public Throwable handleError(RetrofitError cause) {
        if (cause.getResponse().getStatus() == HttpStatus.SC_UNAUTHORIZED) {
            this.sessionManager.logoutUser();
        }

        return cause;
    }
}
