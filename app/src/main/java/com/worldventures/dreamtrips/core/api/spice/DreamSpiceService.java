package com.worldventures.dreamtrips.core.api.spice;

import com.octo.android.robospice.persistence.exception.SpiceException;
import com.octo.android.robospice.retrofit.RetrofitGsonSpiceService;
import com.worldventures.dreamtrips.BuildConfig;
import com.worldventures.dreamtrips.DreamTripsApplication;
import com.worldventures.dreamtrips.core.api.AuthApi;
import com.worldventures.dreamtrips.core.api.DreamTripsApi;
import com.worldventures.dreamtrips.core.api.S3Api;
import com.worldventures.dreamtrips.core.api.SharedServicesApi;

import javax.inject.Inject;

public class DreamSpiceService extends RetrofitGsonSpiceService {

    @Inject
    DreamTripsApi dreamTripsApi;

    @Inject
    SharedServicesApi sharedServicesApi;

    @Inject
    S3Api s3Api;

    @Inject
    AuthApi authApi;

    @Override
    public void onCreate() {
        super.onCreate();
        ((DreamTripsApplication) getApplicationContext()).inject(this);
        addRetrofitInterface(DreamTripsApi.class);
        addRetrofitInterface(SharedServicesApi.class);
        addRetrofitInterface(S3Api.class);
        addRetrofitInterface(AuthApi.class);
    }

    @Override
    protected String getServerUrl() {
        return BuildConfig.AuthApiUrl;
    }

    @SuppressWarnings("unchecked")
    protected <T> T getRetrofitService(Class<T> serviceClass) {
        T t = null;
        if (serviceClass == DreamTripsApi.class) {
            t = (T) dreamTripsApi;
        } else if (serviceClass == SharedServicesApi.class) {
            t = (T) sharedServicesApi;
        } else if (serviceClass == S3Api.class) {
            t = (T) s3Api;
        } else if (serviceClass == AuthApi.class) {
            t = (T) authApi;
        }
        return t;
    }


    public static interface Callback<T> {
        void result(T obj, SpiceException spiceException);
    }

}
