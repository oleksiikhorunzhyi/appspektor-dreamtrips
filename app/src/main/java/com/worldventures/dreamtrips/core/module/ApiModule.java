package com.worldventures.dreamtrips.core.module;

import android.content.Context;

import com.google.gson.FieldNamingPolicy;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.squareup.okhttp.OkHttpClient;
import com.worldventures.dreamtrips.BuildConfig;
import com.worldventures.dreamtrips.core.api.AuthApi;
import com.worldventures.dreamtrips.core.api.DefaultErrorHandler;
import com.worldventures.dreamtrips.core.api.DreamTripsApi;
import com.worldventures.dreamtrips.core.api.S3Api;
import com.worldventures.dreamtrips.core.api.SharedServicesApi;
import com.worldventures.dreamtrips.core.session.AppSessionHolder;
import com.worldventures.dreamtrips.core.session.UserSession;
import com.worldventures.dreamtrips.utils.PersistentCookieStore;

import java.net.CookieManager;
import java.net.CookiePolicy;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;
import retrofit.RequestInterceptor;
import retrofit.RestAdapter;
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


    @Provides
    DreamTripsApi provideApi(RestAdapter adapter) {
        return adapter.create(DreamTripsApi.class);
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
                UserSession userSession = appSessionHolder.get().get();
                String authToken = "Token token=" + userSession.getApiToken();
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
    OkHttpClient provideOkHttpClient(Context context) {
        OkHttpClient okHttpClient = new OkHttpClient();
        CookieManager cookieManager = new CookieManager(new PersistentCookieStore(context), CookiePolicy.ACCEPT_ALL);
        okHttpClient.setCookieHandler(cookieManager);
        return okHttpClient;
    }

}