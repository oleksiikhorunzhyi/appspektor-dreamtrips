package com.worldventures.dreamtrips.core.api.error;

import android.content.Context;

import com.octo.android.robospice.persistence.exception.SpiceException;
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
        if (cause.getKind() == RetrofitError.Kind.NETWORK) {
            return new DtApiException(context.getString(R.string.no_connection), cause);
        } else {
            if (cause.getResponse() == null) {
                return new DtApiException(context.getString(R.string.smth_went_wrong), cause);
            } else {

                try {
                    ErrorResponse errorResponse = (ErrorResponse) cause.getBodyAs(ErrorResponse.class);
                    return new DtApiException(errorResponse, cause.getResponse().getStatus(), cause);
                } catch (Exception ex) {
                    Timber.e(ex, "Something went wrong while parsing responseh");
                    return new DtApiException(context.getString(R.string.smth_went_wrong), cause);
                }
            }
        }
    }
}
