package com.worldventures.dreamtrips.social.ui.background_uploading;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.worldventures.core.model.session.SessionHolder;
import com.worldventures.core.modules.auth.service.ReLoginInteractor;
import com.worldventures.core.service.AuthRetryPolicy;
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
import io.techery.janet.okhttp3.OkClient;
import io.techery.mappery.MapperyContext;
import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
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
         AuthRetryPolicy retryPolicy, ReLoginInteractor reLoginInteractor, Observable<Device> deviceSource) {

      final Janet.Builder builder = new Janet.Builder();
      final Gson gson = new GsonBuilder().create();

      builder.addService(new VideoHttpService(BuildConfig.VIDEO_MICROSERVICE_URL, httpClient, new GsonConverter(gson),
            appSessionHolder, mapperyContext, appVersionNameBuilder, retryPolicy, reLoginInteractor, deviceSource));

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
      OkHttpClient.Builder okHttpClientBuilder = new OkHttpClient.Builder();
      if (BuildConfig.DEBUG) {
         okHttpClientBuilder.interceptors().add(interceptor);
      }
      okHttpClientBuilder.connectTimeout(BuildConfig.API_TIMEOUT_SEC, TimeUnit.SECONDS);
      okHttpClientBuilder.readTimeout(BuildConfig.API_TIMEOUT_SEC, TimeUnit.SECONDS);
      okHttpClientBuilder.writeTimeout(BuildConfig.API_TIMEOUT_SEC, TimeUnit.SECONDS);
      return okHttpClientBuilder.build();
   }

   @Named(JANET_QUALIFIER)
   @Provides
   Interceptor interceptor() {
      HttpLoggingInterceptor logging = new HttpLoggingInterceptor();
      logging.setLevel(HttpLoggingInterceptor.Level.HEADERS);
      return logging;
   }
}
