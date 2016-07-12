package com.worldventures.dreamtrips.core.api;

import android.app.Application;
import android.content.Context;

import com.octo.android.robospice.networkstate.NetworkStateChecker;
import com.octo.android.robospice.persistence.CacheManager;
import com.octo.android.robospice.persistence.exception.CacheCreationException;
import com.octo.android.robospice.retrofit.RetrofitGsonSpiceService;
import com.techery.spares.module.Injector;
import com.techery.spares.module.qualifier.Global;
import com.worldventures.dreamtrips.BuildConfig;
import com.worldventures.dreamtrips.core.utils.events.AppConfigUpdatedEvent;

import javax.inject.Inject;
import javax.inject.Provider;

import dagger.Lazy;
import de.greenrobot.event.EventBus;
import retrofit.converter.Converter;
import retrofit.converter.GsonConverter;

public class DreamSpiceService extends RetrofitGsonSpiceService {

    @Inject
    protected GsonConverter gsonConverter;

    @Inject
    protected DreamTripsApi dreamTripsApi;

    @Inject
    protected Provider<UploaderyApi> uploaderyApiLazy;
    protected UploaderyApi uploaderyApi;

    @Inject
    protected Lazy<SharedServicesApi> sharedServicesApi;

    @Global
    @Inject
    EventBus eventBus;


    @Override
    protected NetworkStateChecker getNetworkStateChecker() {
        return new NetworkStateChecker() {
            @Override
            public boolean isNetworkAvailable(Context context) {
                return true;
            }

            @Override
            public void checkPermissions(Context context) {
                //nothing to do here
            }
        };
    }

    @Override
    public void onCreate() {
        ((Injector) getApplicationContext()).inject(this);
        super.onCreate();
        addRetrofitInterface(DreamTripsApi.class);
        addRetrofitInterface(SharedServicesApi.class);
        uploaderyApi = uploaderyApiLazy.get(); //be careful. should be in UI thread.
        eventBus.register(this);
    }

    @Override
    public void onDestroy() {
        eventBus.unregister(this);
        super.onDestroy();
    }

    @Override
    public CacheManager createCacheManager(Application application) throws CacheCreationException {
        return super.createCacheManager(application);
    }

    public void onEventMainThread(AppConfigUpdatedEvent event) {
        uploaderyApi = uploaderyApiLazy.get();
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
            t = (T) sharedServicesApi.get();
        } else if (serviceClass == UploaderyApi.class) {
            t = (T) uploaderyApi;
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
}
