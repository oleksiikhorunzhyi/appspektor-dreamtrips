package com.worldventures.dreamtrips.modules.dtl.api;

import com.octo.android.robospice.request.retrofit.RetrofitSpiceRequest;
import com.worldventures.dreamtrips.core.api.DtlApi;

public abstract class DtlRequest<T> extends RetrofitSpiceRequest<T, DtlApi> {

    public DtlRequest(Class<T> clazz) {
        super(clazz, DtlApi.class);
    }

}
