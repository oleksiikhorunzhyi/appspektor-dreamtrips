package com.worldventures.dreamtrips.core.api.request;

import android.support.annotation.StringRes;

import com.octo.android.robospice.request.retrofit.RetrofitSpiceRequest;
import com.worldventures.dreamtrips.core.api.DreamTripsApi;

public abstract class DreamTripsRequest<T> extends RetrofitSpiceRequest<T, DreamTripsApi> {

    public static final long DELTA_TRIP = 30 * 60 * 1000L;

    public DreamTripsRequest(Class<T> clazz) {
        super(clazz, DreamTripsApi.class);
    }

    @StringRes
    public int getErrorMessage() {
        return 0;
    }
}

