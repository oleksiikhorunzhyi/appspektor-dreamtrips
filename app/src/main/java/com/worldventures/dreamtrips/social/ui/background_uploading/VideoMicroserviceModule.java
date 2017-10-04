package com.worldventures.dreamtrips.social.ui.background_uploading;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.squareup.okhttp.Interceptor;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.logging.HttpLoggingInterceptor;
import com.worldventures.core.model.session.SessionHolder;
import com.worldventures.core.modules.auth.service.ReLoginInteractor;
import com.worldventures.core.utils.AppVersionNameBuilder;
import com.worldventures.dreamtrips.BuildConfig;
import com.worldventures.dreamtrips.api.session.model.Device;
import com.worldventures.dreamtrips.social.ui.background_uploading.service.VideoHttpService;

import java.util.concurrent.TimeUnit;

import javax.inject.Named;
import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;
import io.techery.janet.Janet;
import io.techery.janet.gson.GsonConverter;
import io.techery.janet.http.HttpClient;
import io.techery.janet.okhttp.OkClient;
import io.techery.mappery.MapperyContext;
import rx.Observable;

@Module(
      injects = {},
      library = true, complete = false)
public class VideoMicroserviceModule {

   public static final String JANET_QUALIFIER = "VIDEO_MICROSERVICE_JANET";

   @Provides
   @Singleton
   @Named(JANET_QUALIFIER)
   Janet provideJanet(@Named(JANET_QUALIFIER) HttpClient httpClient, SessionHolder appSessionHolder,
         MapperyContext mapperyContext, AppVersionNameBuilder appVersionNameBuilder,
         ReLoginInteractor reLoginInteractor, Observable<Device> deviceSource) {

      final Janet.Builder builder = new Janet.Builder();
      final Gson gson = new GsonBuilder().create();

      builder.addService(new VideoHttpService(BuildConfig.VIDEO_MICROSERVICE_URL, httpClient, new GsonConverter(gson),
            appSessionHolder, mapperyContext, appVersionNameBuilder, reLoginInteractor, deviceSource));

      return builder.build();
   }

   @Provides
   @Named(JANET_QUALIFIER)
   HttpClient provideJanetHttpClient(@Named(JANET_QUALIFIER) OkHttpClient okHttpClient) {
      return new OkClient(okHttpClient);
   }

   @Named(JANET_QUALIFIER)
   @Provides
   OkHttpClient provideJanetOkHttpClient(@Named(JANET_QUALIFIER) Interceptor interceptor) {
      OkHttpClient okHttpClient = new OkHttpClient();
      if (BuildConfig.DEBUG) okHttpClient.interceptors().add(interceptor);
      okHttpClient.setConnectTimeout(BuildConfig.API_TIMEOUT_SEC, TimeUnit.SECONDS);
      okHttpClient.setReadTimeout(BuildConfig.API_TIMEOUT_SEC, TimeUnit.SECONDS);
      okHttpClient.setWriteTimeout(BuildConfig.API_TIMEOUT_SEC, TimeUnit.SECONDS);
      return okHttpClient;
   }

   @Named(JANET_QUALIFIER)
   @Provides
   Interceptor interceptor() {
      HttpLoggingInterceptor logging = new HttpLoggingInterceptor();
      logging.setLevel(HttpLoggingInterceptor.Level.HEADERS);
      return logging;
   }
}
