package com.worldventures.dreamtrips.core.janet;

import android.content.Context;

import com.google.gson.Gson;
import com.squareup.okhttp.Interceptor;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.logging.HttpLoggingInterceptor;
import com.techery.spares.module.qualifier.ForApplication;
import com.worldventures.dreamtrips.BuildConfig;
import com.worldventures.dreamtrips.core.janet.cache.CacheResultWrapper;
import com.worldventures.dreamtrips.core.janet.dagger.DaggerActionServiceWrapper;

import java.net.CookieManager;
import java.util.Set;
import java.util.concurrent.TimeUnit;

import javax.inject.Named;
import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;
import io.techery.janet.ActionService;
import io.techery.janet.CommandActionService;
import io.techery.janet.Janet;
import io.techery.janet.http.HttpClient;

@Module(
        includes = {JanetCommandModule.class, JanetServiceModule.class},
        complete = false, library = true)
public class JanetModule {
    public static final String JANET_QUALIFIER = "JANET";
    @Singleton
    @Provides(type = Provides.Type.SET)
    ActionService provideCommandService() {
        return new CommandActionService();
    }

    @Singleton
    @Provides
    Janet provideJanet(Set<ActionService> services, @ForApplication Context context) {
        Janet.Builder builder = new Janet.Builder();
        for (ActionService service : services) {
            service = new DaggerActionServiceWrapper(service, context);
            service = new TimberServiceWrapper(service);
            service = new CacheResultWrapper(service);
            builder.addService(service);
        }
        return builder.build();
    }

    @Named(JANET_QUALIFIER)
    @Provides
    OkHttpClient provideJanetOkHttpClient(CookieManager cookieManager, @Named(JANET_QUALIFIER) Interceptor interceptor) {
        OkHttpClient okHttpClient = new OkHttpClient();
        okHttpClient.setCookieHandler(cookieManager);
        okHttpClient.interceptors().add(interceptor);
        //Currently `api/{uid}/likes` (10k+ms)
        okHttpClient.setConnectTimeout(BuildConfig.API_TIMEOUT_SEC, TimeUnit.SECONDS);
        okHttpClient.setReadTimeout(BuildConfig.API_TIMEOUT_SEC, TimeUnit.SECONDS);
        okHttpClient.setWriteTimeout(BuildConfig.API_TIMEOUT_SEC, TimeUnit.SECONDS);
        return okHttpClient;
    }

    @Named(JANET_QUALIFIER)
    @Provides
    Interceptor interceptor() {
        HttpLoggingInterceptor logging = new HttpLoggingInterceptor();
        logging.setLevel(HttpLoggingInterceptor.Level.BODY);
        return logging;
    }

    @Singleton
    @Provides
    HttpClient provideJanetHttpClient(@Named(JANET_QUALIFIER) OkHttpClient okHttpClient) {
        return new io.techery.janet.okhttp.OkClient(okHttpClient);
    }

    @Provides(type = Provides.Type.SET)
    ActionService provideHttpUploaderService(@ForApplication Context appContext, HttpClient httpClient, Gson gson) {
        return new DreamTripsHttpService(appContext, BuildConfig.DreamTripsApi, httpClient, new io.techery.janet.gson.GsonConverter(gson));
    }

}
