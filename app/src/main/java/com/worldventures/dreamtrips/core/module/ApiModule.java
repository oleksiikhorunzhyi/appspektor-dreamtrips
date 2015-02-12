package com.worldventures.dreamtrips.core.module;

import com.google.gson.FieldNamingPolicy;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.squareup.okhttp.OkHttpClient;
import com.techery.spares.application.BaseApplicationWithInjector;
import com.techery.spares.module.Annotations.Private;
import com.worldventures.dreamtrips.BuildConfig;
import com.worldventures.dreamtrips.core.api.AuthApi;
import com.worldventures.dreamtrips.core.api.DefaultErrorHandler;
import com.worldventures.dreamtrips.core.api.DreamTripsApi;
import com.worldventures.dreamtrips.core.api.DreamTripsApiProxy;
import com.worldventures.dreamtrips.core.api.S3Api;
import com.worldventures.dreamtrips.core.api.SharedServicesApi;
import com.worldventures.dreamtrips.core.api.WorldVenturesApi;
import com.worldventures.dreamtrips.core.session.AppSessionHolder;
import com.worldventures.dreamtrips.utils.RealmGsonExlusionStrategy;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;
import retrofit.RequestInterceptor;
import retrofit.RestAdapter;
import retrofit.client.Header;
import retrofit.client.OkClient;
import retrofit.converter.GsonConverter;

@Module(injects =
        {

        },
        complete = false,
        library = true
)
public class ApiModule {

    public ApiModule() {

    }

    @Private
    @Provides
    DreamTripsApi provideApi(RestAdapter adapter) {
        return adapter.create(DreamTripsApi.class);
    }

    @Provides
    DreamTripsApi provideApiProxy(BaseApplicationWithInjector injector) {
        return new DreamTripsApiProxy(injector);
    }

    @Provides
    RestAdapter provideRestAdapter(GsonConverter gsonConverter, RequestInterceptor requestInterceptor, OkClient okClient, DefaultErrorHandler defaultErrorHandler) {
        return new RestAdapter.Builder()
                .setEndpoint(BuildConfig.DreamTripsApi)
                .setLogLevel(RestAdapter.LogLevel.FULL)
                .setConverter(gsonConverter)
                .setClient(okClient)
                .setRequestInterceptor(requestInterceptor)
                        //.setErrorHandler(defaultErrorHandler)
                .build();
    }

    @Provides
    DefaultErrorHandler provideDefaultErrorHandler(AppSessionHolder appSessionHolder) {
        return new DefaultErrorHandler(appSessionHolder);
    }

    @Provides
    RequestInterceptor provideRequestInterceptor(AppSessionHolder appSessionHolder) {
        return request -> {
            if (appSessionHolder.get().isPresent()) {
                String authToken = "Token token=" + appSessionHolder.get().get().getApiToken();
                for (Header header : appSessionHolder.get().get().getHeaderList()) {
                    request.addHeader(header.getName(), header.getValue());
                }
                request.addHeader("Authorization", authToken);
            }
        };
    }

    @Provides
    GsonConverter provideGsonConverter(Gson gson) {
        return new GsonConverter(gson);
    }

    @Provides
    Gson provideGson() {
        return new GsonBuilder()
                .setExclusionStrategies(new RealmGsonExlusionStrategy())
                .setDateFormat("yyyy-MM-dd")
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
    S3Api provideS3Api() {
        RestAdapter adapter = new RestAdapter.Builder()
                .setEndpoint(S3Api.DEFAULT_URL)
                .setLogLevel(RestAdapter.LogLevel.FULL)
                .build();
        return adapter.create(S3Api.class);
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

    @Provides
    OkClient provideOkClient(OkHttpClient okHttpClient) {
        return new OkClient(okHttpClient);
    }

    @Provides
    OkHttpClient provideOkHttpClient() {
        return new OkHttpClient();
    }

}