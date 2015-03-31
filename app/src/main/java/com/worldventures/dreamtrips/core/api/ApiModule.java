package com.worldventures.dreamtrips.core.api;

import android.content.Context;

import com.google.gson.FieldNamingPolicy;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.squareup.okhttp.OkHttpClient;
import com.techery.spares.session.SessionHolder;
import com.worldventures.dreamtrips.BuildConfig;
import com.worldventures.dreamtrips.core.session.UserSession;
import com.worldventures.dreamtrips.core.utils.PersistentCookieStore;

import java.net.CookieManager;
import java.net.CookiePolicy;
import java.util.Date;

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
    RestAdapter provideRestAdapter(GsonConverter gsonConverter, RequestInterceptor requestInterceptor, OkClient okClient) {
        return new RestAdapter.Builder()
                .setEndpoint(BuildConfig.DreamTripsApi)
                .setLogLevel(RestAdapter.LogLevel.FULL)
                .setConverter(gsonConverter)
                .setClient(okClient)
                .setRequestInterceptor(requestInterceptor)
                .build();
    }

    @Provides
    RequestInterceptor provideRequestInterceptor(Context context, SessionHolder<UserSession> appSessionHolder) {
        return request -> {
            if (appSessionHolder.get().isPresent()) {
                UserSession userSession = appSessionHolder.get().get();
                String authToken = "Token token=" + userSession.getApiToken();
                request.addHeader("Authorization", authToken);
            }
            request.addHeader("Accept-Language", context.getResources().getConfiguration().locale.getLanguage());
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
                .registerTypeAdapter(Date.class, new DateTimeDeserializer())
                .registerTypeAdapter(Date.class, new DateTimeSerializer())

                .create();
    }

    @Provides
    @Singleton
    ConfigApi provideS3Api() {
        return createRestAdapter(BuildConfig.S3Api).create(ConfigApi.class);
    }

    private RestAdapter createRestAdapter(String endpoint) {
        return new RestAdapter.Builder()
                .setEndpoint(endpoint)
                .setLogLevel(RestAdapter.LogLevel.FULL)
                .build();
    }

    @Provides
    @Singleton
    SharedServicesApi provideSharedServicesApi() {
        return createRestAdapter(BuildConfig.SharedServicesApi).create(SharedServicesApi.class);
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