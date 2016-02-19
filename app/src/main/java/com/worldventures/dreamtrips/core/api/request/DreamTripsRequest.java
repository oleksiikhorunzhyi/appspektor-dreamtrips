package com.worldventures.dreamtrips.core.api.request;

import android.content.Context;
import android.support.annotation.StringRes;

import com.octo.android.robospice.request.retrofit.RetrofitSpiceRequest;
import com.worldventures.dreamtrips.core.api.DreamTripsApi;

import javax.inject.Inject;

public abstract class DreamTripsRequest<T> extends RetrofitSpiceRequest<T, DreamTripsApi> {
    public static final long DELTA_TRIP = 30 * 60 * 1000L;

    @Inject
    Context context;

    public DreamTripsRequest(Class<T> clazz) {
        super(clazz, DreamTripsApi.class);
    }

    @StringRes
    public int getErrorMessage() {
        return 0;
    }
}

