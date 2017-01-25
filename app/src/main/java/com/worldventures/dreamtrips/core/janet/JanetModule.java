package com.worldventures.dreamtrips.core.janet;

import android.content.Context;

import com.google.gson.Gson;
import com.squareup.okhttp.Interceptor;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.logging.HttpLoggingInterceptor;
import com.techery.spares.module.qualifier.ForApplication;
import com.worldventures.dreamtrips.BuildConfig;
import com.worldventures.dreamtrips.api.api_common.converter.GsonProvider;
import com.worldventures.dreamtrips.core.janet.api_lib.NewDreamTripsHttpService;
import com.worldventures.dreamtrips.core.janet.cache.CacheActionStorageModule;
import com.worldventures.dreamtrips.core.janet.cache.CacheResultWrapper;
import com.worldventures.dreamtrips.core.janet.cache.CachedAction;
import com.worldventures.dreamtrips.core.janet.cache.storage.ActionStorage;
import com.worldventures.dreamtrips.core.janet.cache.storage.MultipleActionStorage;
import com.worldventures.dreamtrips.core.janet.dagger.DaggerActionServiceWrapper;
import com.worldventures.dreamtrips.core.utils.tracksystem.Tracker;
import com.worldventures.dreamtrips.wallet.di.MagstripeReaderModule;
import com.worldventures.dreamtrips.wallet.di.SmartCardModule;
import com.worldventures.dreamtrips.wallet.util.TimberLogger;

import java.net.CookieManager;
import java.util.List;
import java.util.Set;
import java.util.concurrent.TimeUnit;

import javax.inject.Named;
import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;
import io.techery.janet.ActionService;
import io.techery.janet.CommandActionService;
import io.techery.janet.Janet;
import io.techery.janet.MagstripeActionService;
import io.techery.janet.SmartCardActionService;
import io.techery.janet.gson.GsonConverter;
import io.techery.janet.http.HttpClient;
import io.techery.janet.magstripe.MagstripeReaderClient;
import io.techery.janet.smartcard.client.SmartCardClient;

@Module(
      includes = {
            JanetCommandModule.class,
            JanetServiceModule.class,
            CacheActionStorageModule.class,
            SmartCardModule.class,
            MagstripeReaderModule.class
      },
      complete = false, library = true)
public class JanetModule {
   public static final String JANET_QUALIFIER = "JANET";
   public static final String JANET_API_LIB = "JANET_API_LIB";
   public static final String JANET_WALLET = "JANET_WALLET";

   @Singleton
   @Provides(type = Provides.Type.SET)
   ActionService provideCommandService(@ForApplication Context context) {
      return new DreamTripsCommandService(context);
   }

   @Singleton
   @Provides
   Janet provideJanet(Set<ActionService> services, Set<ActionStorage> cacheStorageSet,
         Set<MultipleActionStorage> multipleActionStorageSet,
         @ForApplication Context context) {
      Janet.Builder builder = new Janet.Builder();
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
         service = new DaggerActionServiceWrapper(service, context);
         builder.addService(service);
      }
      return builder.build();
   }

   @Singleton
   @Provides
   @Named(JANET_API_LIB)
   Janet provideApiLibJanet(@Named(JANET_API_LIB) ActionService httpActionService) {
      Janet.Builder builder = new Janet.Builder();
      builder.addService(new TimberServiceWrapper(httpActionService));
      return builder.build();
   }

   @Singleton
   @Named(JANET_API_LIB)
   @Provides
   SessionActionPipeCreator provideSessionApiLibActionPipeCreator(@Named(JANET_API_LIB) Janet janet) {
      return new SessionActionPipeCreator(janet);
   }

   @Singleton
   @Provides
   SessionActionPipeCreator provideSessionActionPipeCreator(Janet janet) {
      return new SessionActionPipeCreator(janet);
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

   @Singleton
   @Provides(type = Provides.Type.SET)
   ActionService provideHttpService(@ForApplication Context appContext, HttpClient httpClient, Gson gson) {
      return new DreamTripsHttpService(appContext, BuildConfig.DreamTripsApi, httpClient, new GsonConverter(gson),
            new GsonConverter(new GsonProvider().provideGson()));
   }

   @Singleton
   @Provides
   @Named(JANET_API_LIB)
   ActionService provideApiLibHttpService(@ForApplication Context appContext, HttpClient httpClient) {
      return new NewDreamTripsHttpService(appContext, BuildConfig.DreamTripsApi, httpClient, new GsonConverter(new GsonProvider()
            .provideGson()));
   }

   @Singleton
   @Provides(type = Provides.Type.SET)
   ActionService provideAnalyticsService(Set<Tracker> trackers) {
      return new AnalyticsService(trackers);
   }

   //
   @Singleton
   @Provides
   @Named(JANET_WALLET)
   Janet provideWalletJanet(
         @Named(JANET_WALLET) Set<ActionService> services, Set<ActionStorage> cacheStorageSet,
         Set<MultipleActionStorage> multipleActionStorageSet, @ForApplication Context context) {
      Janet.Builder builder = new Janet.Builder();
      for (ActionService service : services) {
         service = new TimberServiceWrapper(service);
         service = new DaggerActionServiceWrapper(service, context);
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
         builder.addService(service);
      }

      return builder.build();
   }

   @Singleton
   @Provides(type = Provides.Type.SET)
   @Named(JANET_WALLET)
   ActionService provideSmartCardService(SmartCardClient client) {
      return new SmartCardActionService.Builder(client)
            .addDefaults()
            .setLogger(new TimberLogger("SC_ABS_LAYER"))
            .setResponseTimeout(TimeUnit.MINUTES.toMillis(2L))
            .build();
   }

   @Singleton
   @Provides(type = Provides.Type.SET)
   @Named(JANET_WALLET)
   ActionService provideWalletCommandService() {
      return new CommandActionService();
   }

   @Singleton
   @Provides(type = Provides.Type.SET)
   @Named(JANET_WALLET)
   ActionService provideMagstripeReaderService(@Named("Mock") MagstripeReaderClient client) {
      return new MagstripeActionService(client);
   }
}
