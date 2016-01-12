package com.worldventures.dreamtrips.modules.common.presenter;

import android.text.TextUtils;

import com.innahema.collections.query.queriables.Queryable;
import com.worldventures.dreamtrips.R;
import com.worldventures.dreamtrips.core.api.error.DtApiException;
import com.worldventures.dreamtrips.core.api.error.ErrorResponse;
import com.worldventures.dreamtrips.modules.common.view.ApiErrorView;

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
        if (!hasView()) return;

        apiErrorView.onApiCallFailed();

        DtApiException dtApiException = null;
        if (exception instanceof DtApiException)
            dtApiException = (DtApiException) exception;
        if (exception.getCause() instanceof DtApiException)
            dtApiException = (DtApiException) exception.getCause();

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

    private void logError(ErrorResponse errorResponse) {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("Fields failed: ");
        Queryable.from(errorResponse.getErrors()).forEachR(entry -> {
            stringBuilder.append("\n");
            stringBuilder.append(entry.field);
            stringBuilder.append(" : ");
            stringBuilder.append(TextUtils.join(",", entry.errors));
        });
        Timber.e(stringBuilder.toString());
    }
}
