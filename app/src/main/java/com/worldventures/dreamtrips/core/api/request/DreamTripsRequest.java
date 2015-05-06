package com.worldventures.dreamtrips.core.api.request;

import com.octo.android.robospice.request.retrofit.RetrofitSpiceRequest;
import com.worldventures.dreamtrips.core.api.DreamTripsApi;

public abstract class DreamTripsRequest<T> extends RetrofitSpiceRequest<T, DreamTripsApi> {
    public static final long DELTA_BUCKET =  60 * 1000L;
    public static final long DELTA_TRIP = 30 * 60 * 1000L;

    public DreamTripsRequest(Class<T> clazz) {
        super(clazz, DreamTripsApi.class);
    }
}
