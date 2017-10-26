package com.worldventures.dreamtrips.core.janet;

import android.content.Context;

import com.worldventures.core.di.qualifier.ForApplication;
import com.worldventures.core.janet.ActionServiceLoggerImpl;
import com.worldventures.core.janet.CommandInjectorImpl;
import com.worldventures.core.janet.Injector;
import com.worldventures.core.janet.SessionActionPipeCreator;
import com.worldventures.core.janet.TimberServiceWrapper;
import com.worldventures.core.janet.cache.CacheResultWrapper;
import com.worldventures.core.janet.cache.CachedAction;
import com.worldventures.core.janet.cache.storage.ActionStorage;
import com.worldventures.core.service.analytics.AnalyticsService;
import com.worldventures.janet.injection.CommandInjector;
import com.worldventures.janet.injection.DaggerActionServiceWrapper;
import com.worldventures.core.service.analytics.Tracker;
import com.worldventures.core.utils.HttpErrorHandlingUtil;
import com.worldventures.dreamtrips.BuildConfig;
import com.worldventures.dreamtrips.core.janet.cache.CacheActionStorageModule;
import com.worldventures.dreamtrips.core.janet.cache.storage.MultipleActionStorage;
import com.worldventures.dreamtrips.social.ui.background_uploading.VideoMicroserviceModule;

import java.net.CookieManager;
import java.util.List;
import java.util.Set;
import java.util.concurrent.TimeUnit;

import javax.inject.Named;
import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;
import io.techery.janet.ActionService;
import io.techery.janet.Janet;
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
            VideoMicroserviceModule.class,
            CacheActionStorageModule.class,
            MobileSdkJanetModule.class,
            AutomationQaConfigModule.class
      },
      complete = false, library = true)
public class JanetModule {

   public static final String JANET_QUALIFIER = "JANET";

   @Singleton
   @Provides
   DreamTripsCommandServiceWrapper provideCommandService(@ForApplication Context context, HttpErrorHandlingUtil util) {
      return new DreamTripsCommandServiceWrapper(context, util);
   }

   @Singleton
   @Provides(type = Provides.Type.SET)
   ActionService provideCommandService(DreamTripsCommandServiceWrapper serviceWrapper) {
      return serviceWrapper;
   }

   @Singleton
   @Provides
   AnalyticsService provideAnalyticsService(Set<Tracker> trackers) {
      return new AnalyticsService(trackers, BuildConfig.ANALYTICS_LOG_ENABLED);
   }

   @Provides(type = Provides.Type.SET)
   ActionService provideAnalyticsService(AnalyticsService analyticsService) {
      return analyticsService;
   }

   @Singleton
   @Provides
   Janet provideJanet(@ForApplication Context context,
         Set<ActionService> services,
         Set<ActionStorage> cacheStorageSet,
         Set<MultipleActionStorage> multipleActionStorageSet) {
      Janet.Builder builder = new Janet.Builder();
      final CommandInjector injector = new CommandInjectorImpl(((Injector) context)
            .getObjectGraph());

      for (ActionService service : services) {
         service = new TimberServiceWrapper(service);
         service = new CacheResultWrapper(service) {{
            for (ActionStorage storage : cacheStorageSet) {
               bindStorage(storage.getActionClass(), storage);
            }

            for (MultipleActionStorage storage : multipleActionStorageSet) {
               List<Class<? extends CachedAction>> cachedActions = storage.getActionClasses();
               for (Class clazz : cachedActions) {
                  bindStorage(clazz, storage);
               }
            }
         }};
         service = new DaggerActionServiceWrapper(service, injector, new ActionServiceLoggerImpl());
         builder.addService(service);
      }
      return builder.build();
   }

   @Singleton
   @Provides
   SessionActionPipeCreator provideSessionActionPipeCreator(Janet janet) {
      return new SessionActionPipeCreator(janet);
   }

   @Named(JANET_QUALIFIER)
   @Provides
   OkHttpClient provideJanetOkHttpClient(CookieManager cookieManager, @Named(JANET_QUALIFIER) Interceptor interceptor) {
      OkHttpClient.Builder okHttpClientBuilder = new OkHttpClient.Builder();
      okHttpClientBuilder.cookieJar(new JavaNetCookieJar(cookieManager));
      okHttpClientBuilder.interceptors().add(interceptor);
      //Currently `api/{uid}/likes` (10k+ms)
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

   @Singleton
   @Provides
   HttpClient provideJanetHttpClient(@Named(JANET_QUALIFIER) OkHttpClient okHttpClient) {
      return new OkClient(okHttpClient);
   }
}
