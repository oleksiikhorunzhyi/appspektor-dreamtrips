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
import io.techery.janet.helper.JanetActionException;
import io.techery.janet.http.exception.HttpServiceException;
import timber.log.Timber;

import static com.worldventures.dreamtrips.util.ThrowableUtils.getCauseByType;

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
        if (exception instanceof CancelException) return;
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
        } else if (!handleJanetHttpError(null, exception)) {
            apiErrorView.informUser(R.string.smth_went_wrong);
        }
    }

    public void handleActionError(Object action, Throwable exception) {
        if (exception instanceof CancelException) return;
        Timber.e(exception, this.getClass().getName() + " handled caught exception");
        if (!hasView()) {
            Crashlytics.logException(exception);
            Timber.e(exception, "ApiErrorPresenter expects apiErrorView to be set, which is null.");
            return;
        }
        //
        if (!handleJanetHttpError(action, exception)) {
            apiErrorView.informUser(R.string.smth_went_wrong);
        }
    }

    private boolean handleJanetHttpError(Object action, Throwable exception) {
        if (action instanceof BaseHttpAction
                && exception instanceof HttpServiceException) {//janet-http
            apiErrorView.onApiCallFailed();
            BaseHttpAction httpAction = (BaseHttpAction) action;
            if (getCauseByType(IOException.class, exception.getCause()) != null) {
                apiErrorView.informUser(R.string.no_connection);
            } else if (httpAction.getErrorResponse() != null) {
                ErrorResponse errorResponse = httpAction.getErrorResponse();
                logError(errorResponse);
                if (!apiErrorView.onApiError(errorResponse))
                    apiErrorView.informUser(errorResponse.getFirstMessage());
            } else {
                apiErrorView.informUser(exception.getCause().getLocalizedMessage());
            }
            return true;
        }
        if (exception instanceof JanetActionException) {
            JanetActionException actionError = (JanetActionException) exception;
            return handleJanetHttpError(actionError.getAction(), actionError.getCause());
        }
        if (exception.getCause() != null) {
            return handleJanetHttpError(action, exception.getCause());
        }
        return false;
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
