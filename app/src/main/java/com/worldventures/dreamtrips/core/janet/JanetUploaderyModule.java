package com.worldventures.dreamtrips.core.janet;

import android.content.Context;

import com.google.gson.Gson;
import com.techery.spares.module.qualifier.ForApplication;
import com.worldventures.dreamtrips.BuildConfig;
import com.worldventures.dreamtrips.api.api_common.converter.GsonProvider;
import com.worldventures.dreamtrips.core.janet.api_lib.NewDreamTripsHttpService;

import java.net.CookieManager;
import java.util.concurrent.TimeUnit;

import javax.inject.Named;
import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;
import io.techery.janet.ActionService;
import io.techery.janet.Janet;
import io.techery.janet.gson.GsonConverter;
import io.techery.janet.http.HttpClient;
import io.techery.janet.okhttp3.OkClient;
import okhttp3.Interceptor;
import okhttp3.JavaNetCookieJar;
import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;

@Module(
      includes = {
            JanetCommandModule.class,
            JanetServiceModule.class,
      },
      complete = false, library = true)
public class JanetUploaderyModule {

   public static final String JANET_UPLOADERY = "JANET_UPLOADERY";

   @Singleton
   @Provides
   @Named(JANET_UPLOADERY)
   Janet provideUploaderyJanet(@Named(JANET_UPLOADERY) ActionService httpActionService) {
      Janet.Builder builder = new Janet.Builder();
      builder.addService(new TimberServiceWrapper(httpActionService));
      return builder.build();
   }

   @Provides
   @Named(JANET_UPLOADERY)
   okhttp3.OkHttpClient provideJanetOkHttp3Client(CookieManager cookieManager,
         @Named(JANET_UPLOADERY) Interceptor interceptor) {
      OkHttpClient.Builder builder = new OkHttpClient.Builder();
      builder.cookieJar(new JavaNetCookieJar(cookieManager));
      if (BuildConfig.DEBUG) builder.addInterceptor(interceptor);
      builder.connectTimeout(BuildConfig.API_TIMEOUT_SEC, TimeUnit.SECONDS);
      builder.readTimeout(BuildConfig.API_TIMEOUT_SEC, TimeUnit.SECONDS);
      builder.writeTimeout(BuildConfig.API_TIMEOUT_SEC, TimeUnit.SECONDS);
      return builder.build();
   }

   @Provides
   @Named(JANET_UPLOADERY)
   Interceptor provideOkHttp3Interceptor() {
      HttpLoggingInterceptor interceptor = new okhttp3.logging.HttpLoggingInterceptor();
      interceptor.setLevel(okhttp3.logging.HttpLoggingInterceptor.Level.BODY);
      return interceptor;
   }

   @Provides
   @Named(JANET_UPLOADERY)
   HttpClient provideJanetHttp3Client(@Named(JANET_UPLOADERY) OkHttpClient httpClient) {
      return new OkClient(httpClient);
   }

   @Provides
   @Named(JANET_UPLOADERY)
   ActionService provideUploaderyActionService(@ForApplication Context appContext, @Named(JANET_UPLOADERY) HttpClient httpClient) {
      return new NewDreamTripsHttpService(appContext, BuildConfig.DreamTripsApi, httpClient, new GsonConverter(new GsonProvider()
            .provideGson()));
   }
}
