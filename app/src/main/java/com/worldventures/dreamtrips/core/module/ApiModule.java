package com.worldventures.dreamtrips.core.module;

import com.google.gson.FieldNamingPolicy;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.worldventures.dreamtrips.core.DataManager;
import com.worldventures.dreamtrips.core.SessionManager;
import com.worldventures.dreamtrips.core.api.AuthApi;
import com.worldventures.dreamtrips.core.api.DreamTripsApi;
import com.worldventures.dreamtrips.core.api.SharedServicesApi;
import com.worldventures.dreamtrips.core.api.WorldVenturesApi;
import com.worldventures.dreamtrips.utils.RealmGsonExlusionStrategy;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;
import retrofit.RestAdapter;
import retrofit.converter.GsonConverter;

@Module(injects =
        {
                DataManager.class
        },
        complete = false
)
public class ApiModule {

    public ApiModule() {

    }

    @Provides
    DreamTripsApi provideApi(RestAdapter adapter) {
        return adapter.create(DreamTripsApi.class);
    }

    @Provides
    RestAdapter provideRestAdapter(GsonConverter gsonConverter, SessionManager sessionManager) {
        return new RestAdapter.Builder()
                .setEndpoint(DreamTripsApi.DEFAULT_URL)
                .setLogLevel(RestAdapter.LogLevel.FULL)
                .setConverter(gsonConverter)
                .setRequestInterceptor(request -> {
                    if (sessionManager.getCurrentSession() != null) {
                        String authToken = "Token token=" + sessionManager.getCurrentSession();
                        request.addHeader("Authorization", authToken);
                    }
                })
                .build();
    }

    @Provides
    GsonConverter provideGsonConverter(Gson gson) {
        return new GsonConverter(gson);
    }

    @Provides
    Gson provideGson() {
        return new GsonBuilder()
                .setExclusionStrategies(new RealmGsonExlusionStrategy())
                .setFieldNamingPolicy(FieldNamingPolicy.LOWER_CASE_WITH_UNDERSCORES)
                .create();
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