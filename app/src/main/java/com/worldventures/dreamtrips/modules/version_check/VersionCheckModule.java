package com.worldventures.dreamtrips.modules.version_check;

import android.content.Context;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.squareup.okhttp.Interceptor;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.logging.HttpLoggingInterceptor;
import com.techery.spares.module.qualifier.ForApplication;
import com.worldventures.dreamtrips.BuildConfig;
import com.worldventures.dreamtrips.core.janet.cache.CacheResultWrapper;
import com.worldventures.dreamtrips.core.janet.cache.storage.ActionStorage;
import com.worldventures.dreamtrips.core.janet.dagger.DaggerActionServiceWrapper;
import com.worldventures.dreamtrips.core.repository.SnappyRepository;
import com.worldventures.dreamtrips.modules.version_check.service.command.VersionCheckCommand;
import com.worldventures.dreamtrips.modules.version_check.service.storage.UpdateRequirementStorage;
import com.worldventures.dreamtrips.modules.version_check.util.VersionComparator;

import java.util.Set;
import java.util.concurrent.TimeUnit;

import javax.inject.Named;
import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;
import io.techery.janet.ActionService;
import io.techery.janet.CommandActionService;
import io.techery.janet.HttpActionService;
import io.techery.janet.Janet;
import io.techery.janet.gson.GsonConverter;
import io.techery.janet.http.HttpClient;

@Module (complete = false, library = true, injects = {
      VersionCheckCommand.class
})
public class VersionCheckModule {

   public static final String JANET_QUALIFIER = "VERSION_CHECK_JANET";

   @Provides
   @Singleton
   @Named(JANET_QUALIFIER)
   Janet provideJanet(@Named(JANET_QUALIFIER) HttpClient httpClient, @ForApplication Context context,
         @Named(JANET_QUALIFIER) Set<ActionStorage> storageSet) {
      Janet.Builder builder = new Janet.Builder();
      Gson gson = new GsonBuilder().create();
      builder.addService(new HttpActionService(BuildConfig.VERSION_CHECK_API_URL, httpClient,
            new GsonConverter(gson)));
      ActionService service = new DaggerActionServiceWrapper(new CommandActionService(), context);
      service = new CacheResultWrapper(service) {{
         for (ActionStorage storage : storageSet) {
            bindStorage(storage.getActionClass(), storage);
         }
      }};
      builder.addService(service);
      return builder.build();
   }

   @Singleton
   @Provides
   @Named(JANET_QUALIFIER)
   HttpClient provideJanetHttpClient(@Named(JANET_QUALIFIER) OkHttpClient okHttpClient) {
      return new io.techery.janet.okhttp.OkClient(okHttpClient);
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
      logging.setLevel(HttpLoggingInterceptor.Level.BODY);
      return logging;
   }

   @Provides
   @Singleton
   VersionComparator provideVersionComparator() {
      return new VersionComparator();
   }

   @Singleton
   @Provides(type = Provides.Type.SET)
   @Named(JANET_QUALIFIER)
   ActionStorage provideStorage(SnappyRepository snappyRepository) {
      return new UpdateRequirementStorage(snappyRepository);
   }
}
