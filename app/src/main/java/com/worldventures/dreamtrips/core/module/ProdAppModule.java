package com.worldventures.dreamtrips.core.module;

import com.worldventures.dreamtrips.core.DataManager;
import com.worldventures.dreamtrips.core.api.AuthApi;
import com.worldventures.dreamtrips.core.api.DreamTripsApi;
import com.worldventures.dreamtrips.core.api.WorldVenturesApi;

import javax.inject.Named;
import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;
import retrofit.RestAdapter;

@Module(injects = DataManager.class)
public class ProdAppModule {

    @Provides
    @Singleton
    @Named("realService")
    DreamTripsApi provideApi() {
        RestAdapter adapter = new RestAdapter.Builder()
                .setEndpoint(DreamTripsApi.DEFAULT_URL)
                .setLogLevel(RestAdapter.LogLevel.BASIC)
                .setRequestInterceptor(request -> request.addHeader("Content-Type", "multipart/form-data; boundary=----AdditionalContentApiAuthRequestBoundary"))
                .build();
        return adapter.create(DreamTripsApi.class);
    }

    @Provides
    @Singleton
    AuthApi provideAuthApi() {
        RestAdapter adapter = new RestAdapter.Builder()
                .setEndpoint(AuthApi.DEFAULT_URL)
                .setLogLevel(RestAdapter.LogLevel.FULL)
                .build();
        return adapter.create(AuthApi.class);
    }

    @Provides
    @Singleton
    WorldVenturesApi provideWorldVenturesApi() {
        RestAdapter adapter = new RestAdapter.Builder()
                .setEndpoint(WorldVenturesApi.DEFAULT_URL)
                .setLogLevel(RestAdapter.LogLevel.FULL)
                .build();
        return adapter.create(WorldVenturesApi.class);
    }
}