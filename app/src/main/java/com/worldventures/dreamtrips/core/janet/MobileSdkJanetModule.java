package com.worldventures.dreamtrips.core.janet;

import android.content.Context;

import com.google.gson.GsonBuilder;
import com.google.gson.TypeAdapterFactory;
import com.techery.spares.module.qualifier.ForApplication;
import com.techery.spares.session.SessionHolder;
import com.worldventures.dreamtrips.BuildConfig;
import com.worldventures.dreamtrips.api.api_common.converter.DateTimeDeserializer;
import com.worldventures.dreamtrips.api.api_common.converter.DateTimeSerializer;
import com.worldventures.dreamtrips.api.api_common.converter.GsonProvider;
import com.worldventures.dreamtrips.api.api_common.converter.SerializedNameExclusionStrategy;
import com.worldventures.dreamtrips.api.api_common.converter.SmartEnumTypeAdapterFactory;
import com.worldventures.dreamtrips.api.api_common.service.MonolithHttpService;
import com.worldventures.dreamtrips.api.session.model.Device;
import com.worldventures.dreamtrips.core.janet.api_lib.AuthStorage;
import com.worldventures.dreamtrips.core.janet.api_lib.CredentialsProvider;
import com.worldventures.dreamtrips.core.janet.api_lib.DreamTripsAuthRefresher;
import com.worldventures.dreamtrips.core.janet.api_lib.DreamTripsAuthStorage;
import com.worldventures.dreamtrips.core.janet.api_lib.DreamTripsCredentialsProvider;
import com.worldventures.dreamtrips.core.session.UserSession;
import com.worldventures.dreamtrips.core.utils.HttpErrorHandlingUtil;
import com.worldventures.dreamtrips.mobilesdk.AuthProviders;
import com.worldventures.dreamtrips.mobilesdk.AuthRefresher;
import com.worldventures.dreamtrips.mobilesdk.ConfigProviders;
import com.worldventures.dreamtrips.mobilesdk.DreamTripsErrorParser;
import com.worldventures.dreamtrips.mobilesdk.DreamtripsApiProvider;
import com.worldventures.dreamtrips.mobilesdk.authentication.AuthData;
import com.worldventures.dreamtrips.mobilesdk.util.HttpErrorReasonParser;
import com.worldventures.dreamtrips.modules.auth.service.ReLoginInteractor;

import java.net.CookieManager;
import java.util.Date;
import java.util.Set;
import java.util.concurrent.TimeUnit;

import javax.inject.Named;
import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;
import io.techery.janet.ActionService;
import io.techery.janet.HttpActionService;
import io.techery.janet.gson.GsonConverter;
import io.techery.janet.http.HttpClient;
import io.techery.janet.okhttp3.OkClient;
import io.techery.mappery.MapperyContext;
import okhttp3.Headers;
import okhttp3.JavaNetCookieJar;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.logging.HttpLoggingInterceptor;
import rx.Observable;
import timber.log.Timber;

@Module(
      includes = {
            ApiConfigModule.class,
      },
      complete = false, library = true)
public class MobileSdkJanetModule {

   private static final String API_QUALIFIER = "MobileSdkJanetModule";
   private static final String NON_API_QUALIFIER = "NonApiMobileSdkJanetModule";
   private static final String LOGGING_TAG = "DT MobileSDK API";

   @Provides(type = Provides.Type.SET)
   ActionService provideApiService(DreamtripsApiProvider dreamtripsApiProvider) {
      return dreamtripsApiProvider.createApiService();
   }

   @Provides
   DreamtripsApiProvider provideDreamTripsApiProvider(
         AuthProviders authProviders, ConfigProviders configProviders,
         AuthRefresher authRefresher, @Named(API_QUALIFIER) HttpClient httpClient,
         @Named(NON_API_QUALIFIER) HttpActionService nonApiService) {
      return new DreamtripsApiProvider(authProviders, configProviders, authRefresher)
            .httpClient(httpClient)
            .nonApiService(nonApiService);
   }

   @Provides
   @Named(NON_API_QUALIFIER)
   HttpActionService provideNonApiService(@Named(API_QUALIFIER) HttpLoggingInterceptor loggingInterceptor, Set<TypeAdapterFactory> adapterFactories) {
      final GsonBuilder gsonBuilder = new GsonBuilder()
            .setExclusionStrategies(new SerializedNameExclusionStrategy())
            //
            .registerTypeAdapterFactory(new SmartEnumTypeAdapterFactory("unknown"))
            .registerTypeAdapter(Date.class, new DateTimeSerializer())
            .registerTypeAdapter(Date.class, new DateTimeDeserializer());
      for (TypeAdapterFactory factory : adapterFactories) {
         gsonBuilder.registerTypeAdapterFactory(factory);
      }
      return new HttpActionService("http://dreamtrips-nonexisting-api.com",
            new OkClient(new OkHttpClient.Builder()
                  .addNetworkInterceptor(loggingInterceptor)
                  .build()
            ), new GsonConverter(gsonBuilder.create())
      );
   }

   @Provides
   @Named(API_QUALIFIER)
   HttpClient provideHttpClient(CookieManager cookieManager, @Named(API_QUALIFIER) HttpLoggingInterceptor loggingInterceptor) {
      OkHttpClient okHttpClient = new OkHttpClient.Builder()
            .cookieJar(new JavaNetCookieJar(cookieManager))
            .addNetworkInterceptor(chain -> {
               Request request = chain.request();
               Headers headers = request.headers().newBuilder().set("Accept-Encoding", "identity").build();
               Request newRequest = request.newBuilder().headers(headers).build();
               return chain.proceed(newRequest);
            })
            .addInterceptor(loggingInterceptor)
            .connectTimeout(BuildConfig.API_TIMEOUT_SEC, TimeUnit.SECONDS)
            .readTimeout(BuildConfig.API_TIMEOUT_SEC, TimeUnit.SECONDS)
            .writeTimeout(BuildConfig.API_TIMEOUT_SEC, TimeUnit.SECONDS)
            .build();
      return new OkClient(okHttpClient);
   }

   @Provides
   @Named(API_QUALIFIER)
   HttpLoggingInterceptor provideLoggingInterceptor() {
      HttpLoggingInterceptor interceptor = new HttpLoggingInterceptor(message -> Timber.tag(LOGGING_TAG).d(message));
      interceptor.setLevel(HttpLoggingInterceptor.Level.BODY);
      return interceptor;
   }

   @Singleton
   @Provides
   AuthRefresher provideDreamTripsAuthRefresher(ReLoginInteractor reLoginInteractor,
         CredentialsProvider credentialsProvider, AuthStorage authStorage, MapperyContext mapperyContext) {
      return new DreamTripsAuthRefresher(reLoginInteractor, credentialsProvider, authStorage, mapperyContext);
   }

   @Singleton
   @Provides
   ReLoginInteractor provideReLoginInteractor(ConfigProviders configProviders, @Named(API_QUALIFIER) HttpClient httpClient) {
      MonolithHttpService authService = new MonolithHttpService(configProviders.monolithConfig(), () -> (AuthData) () -> null,
            httpClient, new GsonConverter(new GsonProvider().provideGson())
      );
      return new ReLoginInteractor(authService);
   }

   @Singleton
   @Provides
   AuthStorage provideDreamTripsAuthStorage(SessionHolder<UserSession> sessionHolder) {
      return new DreamTripsAuthStorage(sessionHolder);
   }

   @Singleton
   @Provides
   CredentialsProvider provideDreamTripsCredentialsProvider(SessionHolder<UserSession> sessionHolder, Observable<Device> deviceSource) {
      return new DreamTripsCredentialsProvider(sessionHolder, deviceSource);
   }

   @Provides
   DreamTripsErrorParser provideDreamTripsErrorParser(@Named(NON_API_QUALIFIER) HttpErrorReasonParser nonApiErrorParser) {
      return new DreamTripsErrorParser(nonApiErrorParser);
   }

   @Provides
   @Named(NON_API_QUALIFIER)
   HttpErrorReasonParser provideNonApiErrorParser() {
      return action -> null;
   }

   @Singleton
   @Provides
   HttpErrorHandlingUtil provideHttpErrorHandlingUtils(@ForApplication Context context, DreamTripsErrorParser errorParser) {
      return new HttpErrorHandlingUtil(context, errorParser);
   }

}
