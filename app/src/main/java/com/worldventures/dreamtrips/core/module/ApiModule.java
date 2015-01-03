package com.worldventures.dreamtrips.core.module;

import com.google.gson.FieldNamingPolicy;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.worldventures.dreamtrips.core.DataManager;
import com.worldventures.dreamtrips.core.api.AuthApi;
import com.worldventures.dreamtrips.core.api.DreamTripsApi;
import com.worldventures.dreamtrips.core.api.SharedServicesApi;
import com.worldventures.dreamtrips.core.api.WorldVenturesApi;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;
import retrofit.RestAdapter;
import retrofit.converter.GsonConverter;

@Module(injects = {DataManager.class})
public class ApiModule {

    public ApiModule() {

    }

    @Provides
    @Singleton
    DreamTripsApi provideApi() {
        RestAdapter adapter = new RestAdapter.Builder()
                .setEndpoint(DreamTripsApi.DEFAULT_URL)
                .setLogLevel(RestAdapter.LogLevel.FULL)
                .setConverter(new GsonConverter(getGson()))
                .build();
        return adapter.create(DreamTripsApi.class);
    }

    private Gson getGson() {
        Gson gson = new GsonBuilder()
                .setFieldNamingPolicy(FieldNamingPolicy.LOWER_CASE_WITH_UNDERSCORES)
                .create();
        return gson;
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

    @Provides
    @Singleton
    SharedServicesApi provideSharedServicesApi() {
        RestAdapter adapter = new RestAdapter.Builder()
                .setEndpoint(SharedServicesApi.DEFAULT_URL)
                .setLogLevel(RestAdapter.LogLevel.FULL)
                .build();
        return adapter.create(SharedServicesApi.class);
    }


}