package com.worldventures.dreamtrips.core.janet;

import com.innahema.collections.query.queriables.Queryable;
import com.worldventures.core.janet.TimberServiceWrapper;
import com.worldventures.core.model.session.SessionHolder;
import com.worldventures.core.modules.auth.service.ReLoginInteractor;
import com.worldventures.core.service.NewDreamTripsHttpService;
import com.worldventures.core.utils.AppVersionNameBuilder;
import com.worldventures.dreamtrips.BuildConfig;
import com.worldventures.dreamtrips.api.api_common.converter.GsonProvider;
import com.worldventures.dreamtrips.api.session.model.Device;
import com.worldventures.dreamtrips.core.api.uploadery.SimpleUploaderyCommand;

import java.net.CookieManager;
import java.util.Set;
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
import io.techery.mappery.MapperyContext;
import okhttp3.Interceptor;
import okhttp3.JavaNetCookieJar;
import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import rx.Observable;

@Module(
      injects = {
            SimpleUploaderyCommand.class,
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
   OkHttpClient provideJanetOkHttp3Client(CookieManager cookieManager, @Named(JANET_UPLOADERY) Set<Interceptor> interceptors) {
      OkHttpClient.Builder builder = new OkHttpClient.Builder();
      builder.cookieJar(new JavaNetCookieJar(cookieManager));
      Queryable.from(interceptors).forEachR(builder::addInterceptor);
      builder.connectTimeout(BuildConfig.API_TIMEOUT_SEC, TimeUnit.SECONDS);
      builder.readTimeout(BuildConfig.API_TIMEOUT_SEC, TimeUnit.SECONDS);
      builder.writeTimeout(BuildConfig.API_TIMEOUT_SEC, TimeUnit.SECONDS);
      return builder.build();
   }

   @Provides
   @Named(JANET_UPLOADERY)
   HttpLoggingInterceptor provideOkHttp3Interceptor() {
      HttpLoggingInterceptor interceptor = new HttpLoggingInterceptor();
      interceptor.setLevel(HttpLoggingInterceptor.Level.BASIC);
      return interceptor;
   }

   @Provides
   @Named(JANET_UPLOADERY)
   HttpClient provideJanetHttp3Client(@Named(JANET_UPLOADERY) OkHttpClient httpClient) {
      return new OkClient(httpClient);
   }

   @Provides
   @Named(JANET_UPLOADERY)
   ActionService provideUploaderyActionService(SessionHolder appSessionHolder, AppVersionNameBuilder appVersionNameBuilder,
         MapperyContext mapperyContext, ReLoginInteractor reLoginInteractor, Observable<Device> deviceSource,
         @Named(JANET_UPLOADERY) HttpClient httpClient) {
      return new NewDreamTripsHttpService(appSessionHolder, appVersionNameBuilder, mapperyContext, reLoginInteractor,
            deviceSource, BuildConfig.DreamTripsApi, httpClient, new GsonConverter(new GsonProvider().provideGson()),
            BuildConfig.API_VERSION);
   }
}
