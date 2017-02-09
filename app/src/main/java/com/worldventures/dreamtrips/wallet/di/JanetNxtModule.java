package com.worldventures.dreamtrips.wallet.di;

import android.content.Context;

import com.techery.spares.module.qualifier.ForApplication;
import com.worldventures.dreamtrips.BuildConfig;
import com.worldventures.dreamtrips.api.api_common.converter.GsonProvider;
import com.worldventures.dreamtrips.core.janet.TimberServiceWrapper;
import com.worldventures.dreamtrips.core.janet.dagger.DaggerActionServiceWrapper;
import com.worldventures.dreamtrips.wallet.service.nxt.DetokenizeBankCardCommand;
import com.worldventures.dreamtrips.wallet.service.nxt.NxtHttpService;
import com.worldventures.dreamtrips.wallet.service.nxt.TokenizeBankCardCommand;
import com.worldventures.dreamtrips.wallet.service.nxt.model.GsonAdaptersMultiRequestBody;
import com.worldventures.dreamtrips.wallet.service.nxt.model.GsonAdaptersMultiRequestElement;
import com.worldventures.dreamtrips.wallet.service.nxt.model.GsonAdaptersMultiResponseBody;
import com.worldventures.dreamtrips.wallet.service.nxt.model.GsonAdaptersMultiResponseElement;

import java.util.Set;
import java.util.concurrent.TimeUnit;

import javax.inject.Named;
import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;
import io.techery.janet.ActionService;
import io.techery.janet.CommandActionService;
import io.techery.janet.Janet;
import io.techery.janet.gson.GsonConverter;
import io.techery.janet.http.HttpClient;
import io.techery.janet.okhttp3.OkClient;
import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;

@Module(
      injects = {
            TokenizeBankCardCommand.class,
            DetokenizeBankCardCommand.class,
            NxtHttpService.class
      },
      complete = false, library = true)
public class JanetNxtModule {

   public static final String JANET_NXT = "JANET_NXT";

   @Singleton
   @Provides
   @Named(JANET_NXT)
   Janet provideJanet(@ForApplication Context context, @Named(JANET_NXT) Set<ActionService> services) {
      Janet.Builder builder = new Janet.Builder();
      for (ActionService service : services) {
         service = new TimberServiceWrapper(service);
         service = new DaggerActionServiceWrapper(service, context);
         builder.addService(service);
      }
      return builder.build();
   }

   @Provides
   @Named(JANET_NXT)
   OkHttpClient provideJanetOkHttp3Client(@Named(JANET_NXT) Interceptor interceptor) {
      OkHttpClient.Builder builder = new OkHttpClient.Builder();
      if (BuildConfig.DEBUG) builder.addInterceptor(interceptor);
      builder.connectTimeout(BuildConfig.API_TIMEOUT_SEC, TimeUnit.SECONDS);
      builder.readTimeout(BuildConfig.API_TIMEOUT_SEC, TimeUnit.SECONDS);
      builder.writeTimeout(BuildConfig.API_TIMEOUT_SEC, TimeUnit.SECONDS);
      return builder.build();
   }

   @Provides
   @Named(JANET_NXT)
   okhttp3.Interceptor provideOkHttp3Interceptor() {
      HttpLoggingInterceptor interceptor = new HttpLoggingInterceptor();
      interceptor.setLevel(HttpLoggingInterceptor.Level.BASIC);
      return interceptor;
   }

   @Provides
   @Named(JANET_NXT)
   HttpClient provideJanetHttp3Client(@Named(JANET_NXT) okhttp3.OkHttpClient httpClient) {
      return new OkClient(httpClient);
   }

   @Singleton
   @Provides(type = Provides.Type.SET)
   @Named(JANET_NXT)
   ActionService provideNxtCommandService() {
      return new CommandActionService();
   }

   @Singleton
   @Provides(type = Provides.Type.SET)
   @Named(JANET_NXT)
   ActionService provideNxtActionService(@ForApplication Context appContext, @Named(JANET_NXT) HttpClient httpClient) {
      return new NxtHttpService(appContext, BuildConfig.NXT_API, httpClient, new GsonConverter(
            new GsonProvider().provideBuilder()
                  .registerTypeAdapterFactory(new GsonAdaptersMultiRequestBody())
                  .registerTypeAdapterFactory(new GsonAdaptersMultiRequestElement())
                  .registerTypeAdapterFactory(new GsonAdaptersMultiResponseBody())
                  .registerTypeAdapterFactory(new GsonAdaptersMultiResponseElement())
                  .create()));
   }

}