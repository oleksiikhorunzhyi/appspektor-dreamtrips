package com.worldventures.dreamtrips.core.api.spice;

import android.app.Application;
import android.content.Context;

import com.octo.android.robospice.networkstate.NetworkStateChecker;
import com.octo.android.robospice.persistence.CacheManager;
import com.octo.android.robospice.persistence.binary.InFileBitmapObjectPersister;
import com.octo.android.robospice.persistence.exception.CacheCreationException;
import com.octo.android.robospice.persistence.exception.SpiceException;
import com.octo.android.robospice.persistence.memory.LruCacheBitmapObjectPersister;
import com.octo.android.robospice.retrofit.RetrofitGsonSpiceService;
import com.worldventures.dreamtrips.BuildConfig;
import com.worldventures.dreamtrips.DreamTripsApplication;
import com.worldventures.dreamtrips.core.api.AuthApi;
import com.worldventures.dreamtrips.core.api.DreamTripsApi;
import com.worldventures.dreamtrips.core.api.S3Api;
import com.worldventures.dreamtrips.core.api.SharedServicesApi;

import javax.inject.Inject;

import retrofit.converter.Converter;
import retrofit.converter.GsonConverter;

public class DreamSpiceService extends RetrofitGsonSpiceService {

    @Inject
    DreamTripsApi dreamTripsApi;

    @Inject
    SharedServicesApi sharedServicesApi;

    @Inject
    S3Api s3Api;

    @Inject
    AuthApi authApi;
    @Inject
    GsonConverter gsonConverter;

    @Override
    protected NetworkStateChecker getNetworkStateChecker() {
        return new NetworkStateChecker() {
            @Override
            public boolean isNetworkAvailable(Context context) {
                return true;
            }

            @Override
            public void checkPermissions(Context context) {

            }
        };
    }

    @Override
    public void onCreate() {
        ((DreamTripsApplication) getApplicationContext()).inject(this);
        super.onCreate();
        addRetrofitInterface(DreamTripsApi.class);
        addRetrofitInterface(SharedServicesApi.class);
        addRetrofitInterface(S3Api.class);
        addRetrofitInterface(AuthApi.class);
    }

    @Override
    public CacheManager createCacheManager(Application application) throws CacheCreationException {
        return super.createCacheManager(application);
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

    @Override
    public int getThreadCount() {
        return 8;
    }

    @Override
    protected Converter createConverter() {
        return gsonConverter;
    }


    public static interface Callback<T> {
        void result(T obj, SpiceException spiceException);
    }

}
