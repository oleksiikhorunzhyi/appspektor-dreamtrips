package com.worldventures.dreamtrips.core.api.error;

import android.content.Context;

import com.messenger.util.CrashlyticsTracker;
import com.octo.android.robospice.persistence.exception.SpiceException;
import com.octo.android.robospice.request.SpiceRequest;
import com.worldventures.dreamtrips.R;

import retrofit.ErrorHandler;
import retrofit.RetrofitError;
import timber.log.Timber;

public class DTErrorHandler implements ErrorHandler {

    private Context context;

    public DTErrorHandler(Context context) {
        this.context = context;
    }

    public Throwable handleSpiceError(SpiceException error) {
        if (error.getCause() instanceof RetrofitError) {
            return handleError((RetrofitError) error.getCause());
        } else {
            return error.getCause();
        }
    }

    @Override
    public Throwable handleError(RetrofitError cause) {
        DtApiException dtApiException;
        if (cause.getKind() == RetrofitError.Kind.NETWORK) {
            dtApiException = new DtApiException(context.getString(R.string.no_connection), cause);
            return dtApiException;
        } else {
            if (cause.getResponse() == null) {
                dtApiException = new DtApiException(context.getString(R.string.smth_went_wrong), cause);
            } else {
                try {
                    ErrorResponse errorResponse = (ErrorResponse) cause.getBodyAs(ErrorResponse.class);
                    dtApiException = new DtApiException(errorResponse, cause.getResponse().getStatus(), cause);
                    switch (cause.getResponse().getStatus()) {
                        case 401:
                        case 422:
                            break;
                        default:
                            CrashlyticsTracker.trackError(cause);
                            break;
                    }
                } catch (Exception ex) {
                    Timber.e(ex, "Something went wrong while parsing response");
                    dtApiException = new DtApiException(context.getString(R.string.smth_went_wrong), cause);
                }
            }
        }
        return dtApiException;
    }
}