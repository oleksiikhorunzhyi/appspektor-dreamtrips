package com.worldventures.dreamtrips.modules.common.presenter;

import android.text.TextUtils;

import com.crashlytics.android.Crashlytics;
import com.innahema.collections.query.queriables.Queryable;
import com.worldventures.dreamtrips.R;
import com.worldventures.dreamtrips.core.api.action.BaseHttpAction;
import com.worldventures.dreamtrips.core.api.error.DtApiException;
import com.worldventures.dreamtrips.core.api.error.ErrorResponse;
import com.worldventures.dreamtrips.modules.common.view.ApiErrorView;

import java.io.IOException;

import io.techery.janet.CancelException;
import io.techery.janet.http.exception.HttpServiceException;
import timber.log.Timber;

public class ApiErrorPresenter {

    ApiErrorView apiErrorView;

    public void setView(ApiErrorView apiErrorView) {
        this.apiErrorView = apiErrorView;
    }

    public void dropView() {
        apiErrorView = null;
    }

    public boolean hasView() {
        return apiErrorView != null;
    }

    public void handleError(Throwable exception) {
        Timber.e(exception, this.getClass().getName() + " handled caught exception");
        if (!hasView()) {
            Crashlytics.logException(exception);
            Timber.e(exception, "ApiErrorPresenter expects apiErrorView to be set, which is null.");
            return;
        }
        //
        apiErrorView.onApiCallFailed();
        //
        DtApiException dtApiException = getCauseByType(DtApiException.class, exception);
        if (dtApiException != null) {
            ErrorResponse errorResponse = dtApiException.getErrorResponse();
            if (errorResponse == null || errorResponse.getErrors() == null
                    || errorResponse.getErrors().isEmpty()) {
                apiErrorView.informUser(exception.getCause().getLocalizedMessage());
                return;
            }
            //
            logError(errorResponse);
            //
            if (!apiErrorView.onApiError(errorResponse))
                apiErrorView.informUser(errorResponse.getFirstMessage());
        } else {
            apiErrorView.informUser(R.string.smth_went_wrong);
        }
    }

    public void handleActionError(BaseHttpAction action, Throwable exception) {
        if (exception instanceof CancelException) return;
        Timber.e(exception, this.getClass().getName() + " handled caught exception");
        if (!hasView()) {
            Crashlytics.logException(exception);
            Timber.e(exception, "ApiErrorPresenter expects apiErrorView to be set, which is null.");
            return;
        }
        apiErrorView.onApiCallFailed();
        //
        if (exception instanceof HttpServiceException) {//janet-http
            if (getCauseByType(IOException.class, exception.getCause()) != null) {
                apiErrorView.informUser(R.string.no_connection);
            } else if (action.getErrorResponse() != null) {
                ErrorResponse errorResponse = action.getErrorResponse();
                logError(errorResponse);
                if (!apiErrorView.onApiError(errorResponse))
                    apiErrorView.informUser(errorResponse.getFirstMessage());
            } else {
                apiErrorView.informUser(exception.getCause().getLocalizedMessage());
            }
        } else {
            apiErrorView.informUser(R.string.smth_went_wrong);
        }
    }

    private <T> T getCauseByType(Class<T> causeType, Throwable exception) {
        if (causeType.isInstance(exception)) {
            return (T) exception;
        }
        if (exception.getCause() != null) {
            return getCauseByType(causeType, exception.getCause());
        }
        return null;
    }

    private void logError(ErrorResponse errorResponse) {
        StringBuilder stringBuilder = new StringBuilder("Fields failed: ");
        //
        Queryable.from(errorResponse.getErrors()).forEachR(entry -> {
            stringBuilder.append("\n")
                    .append(entry.field)
                    .append(" : ")
                    .append(TextUtils.join(",", entry.errors));
        });
        //
        Timber.e(stringBuilder.toString());
    }
}
