package com.worldventures.dreamtrips.core.api;

import android.app.Application;
import android.content.Context;

import com.octo.android.robospice.networkstate.NetworkStateChecker;
import com.octo.android.robospice.persistence.CacheManager;
import com.octo.android.robospice.persistence.exception.CacheCreationException;
import com.octo.android.robospice.persistence.exception.SpiceException;
import com.octo.android.robospice.retrofit.RetrofitGsonSpiceService;
import com.worldventures.dreamtrips.App;
import com.worldventures.dreamtrips.BuildConfig;
import com.worldventures.dreamtrips.core.api.ConfigApi;
import com.worldventures.dreamtrips.core.api.DreamTripsApi;
import com.worldventures.dreamtrips.core.api.SharedServicesApi;

import javax.inject.Inject;

import retrofit.converter.Converter;
import retrofit.converter.GsonConverter;

public class DreamSpiceService extends RetrofitGsonSpiceService {

    @Inject
    GsonConverter gsonConverter;

    @Inject
    DreamTripsApi dreamTripsApi;

    @Inject
    SharedServicesApi sharedServicesApi;

    @Inject
    ConfigApi configApi;

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
        ((App) getApplicationContext()).inject(this);
        super.onCreate();
        addRetrofitInterface(DreamTripsApi.class);
        addRetrofitInterface(SharedServicesApi.class);
        addRetrofitInterface(ConfigApi.class);
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
        } else if (serviceClass == ConfigApi.class) {
            t = (T) configApi;
        }
        return t;
    }

    @Override
    public int getThreadCount() {
        return 4;
    }

    @Override
    protected Converter createConverter() {
        return gsonConverter;
    }


    public static interface Callback<T> {
        void result(T obj, SpiceException spiceException);
    }

}
