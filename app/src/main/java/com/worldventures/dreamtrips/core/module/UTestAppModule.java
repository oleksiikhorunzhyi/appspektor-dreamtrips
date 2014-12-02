package com.worldventures.dreamtrips.core.module;

import com.worldventures.dreamtrips.core.DataManager;
import com.worldventures.dreamtrips.core.DreamTripsApi;

import javax.inject.Named;
import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;
import retrofit.RestAdapter;

//@Module(injects = DataManager.class)
public class UTestAppModule {
   /* @Provides
    @Singleton
    @Named("utestService")
    DreamTripsApi provideApi() {
        RestAdapter adapter = new RestAdapter.Builder()
                .setEndpoint(DreamTripsApi.DEFAULT_URL)
                .setLogLevel(RestAdapter.LogLevel.BASIC)
                .setRequestInterceptor(request -> request.addHeader("Content-Type", "multipart/form-data; boundary=----AdditionalContentApiAuthRequestBoundary"))
                .build();
        return adapter.create(DreamTripsApi.class);
    }*/
}