package com.worldventures.dreamtrips.modules.common.presenter;

import android.text.TextUtils;

import com.innahema.collections.query.queriables.Queryable;
import com.octo.android.robospice.persistence.exception.SpiceException;
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
        return apiErrorView == null;
    }

    public void handleError(SpiceException spiceException) {
        if (!hasView()) return;

        if (spiceException.getCause() instanceof DtApiException) {
            ErrorResponse errorResponse = ((DtApiException) spiceException.getCause()).getErrorResponse();
            if (errorResponse == null || errorResponse.getErrors() == null
                    || errorResponse.getErrors().isEmpty()) {
                apiErrorView.informUser(spiceException.getCause().getLocalizedMessage());
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
