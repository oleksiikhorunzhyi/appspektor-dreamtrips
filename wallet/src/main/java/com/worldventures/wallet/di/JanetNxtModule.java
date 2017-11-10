package com.worldventures.wallet.di;

import android.content.Context;

import com.worldventures.core.di.qualifier.ForApplication;
import com.worldventures.core.janet.ActionServiceLoggerImpl;
import com.worldventures.core.janet.CommandInjectorImpl;
import com.worldventures.core.janet.Injector;
import com.worldventures.core.janet.TimberServiceWrapper;
import com.worldventures.core.utils.AppVersionNameBuilder;
import com.worldventures.dreamtrips.api.api_common.converter.GsonProvider;
import com.worldventures.janet.injection.CommandInjector;
import com.worldventures.janet.injection.DaggerActionServiceWrapper;
import com.worldventures.wallet.domain.session.NxtSessionHolder;
import com.worldventures.wallet.service.WalletSocialInfoProvider;
import com.worldventures.wallet.service.nxt.DetokenizeMultipleRecordsCommand;
import com.worldventures.wallet.service.nxt.DetokenizeRecordCommand;
import com.worldventures.wallet.service.nxt.NxtHttpService;
import com.worldventures.wallet.service.nxt.NxtIdConfigsProvider;
import com.worldventures.wallet.service.nxt.TokenizeMultipleRecordsCommand;
import com.worldventures.wallet.service.nxt.TokenizeRecordCommand;
import com.worldventures.wallet.util.WalletBuildConfigHelper;

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
import io.techery.mappery.MapperyContext;
import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;

@Module(
      injects = {
            TokenizeRecordCommand.class,
            DetokenizeRecordCommand.class,
            TokenizeMultipleRecordsCommand.class,
            DetokenizeMultipleRecordsCommand.class,
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
      final CommandInjector injector = new CommandInjectorImpl(((Injector) context)
            .getObjectGraph());
      for (ActionService service : services) {
         service = new TimberServiceWrapper(service);
         service = new DaggerActionServiceWrapper(service, injector, new ActionServiceLoggerImpl());
         builder.addService(service);
      }
      return builder.build();
   }

   @Provides
   @Named(JANET_NXT)
   OkHttpClient provideJanetOkHttp3Client(@Named(JANET_NXT) Interceptor interceptor,
         NxtIdConfigsProvider nxtIdConfigsProvider, WalletBuildConfigHelper configHelper) {
      OkHttpClient.Builder builder = new OkHttpClient.Builder();
      if (configHelper.isDebug()) {
         builder.addInterceptor(interceptor);
      }
      builder.connectTimeout(nxtIdConfigsProvider.apiTimeoutSec(), TimeUnit.SECONDS);
      builder.readTimeout(nxtIdConfigsProvider.apiTimeoutSec(), TimeUnit.SECONDS);
      builder.writeTimeout(nxtIdConfigsProvider.apiTimeoutSec(), TimeUnit.SECONDS);
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
   ActionService provideNxtActionService(NxtSessionHolder nxtSessionHolder, AppVersionNameBuilder appVersionNameBuilder,
         WalletSocialInfoProvider socialInfoProvider, MapperyContext mapperyContext, NxtIdConfigsProvider nxtIdConfigsProvider,
         @Named(JANET_NXT) HttpClient httpClient) {
      return new NxtHttpService(nxtSessionHolder, appVersionNameBuilder, socialInfoProvider, mapperyContext,
            nxtIdConfigsProvider.nxtidApi(), httpClient, new GsonConverter(
            new GsonProvider().provideBuilder()
//                  .registerTypeAdapterFactory(new GsonAdaptersMultiRequestBody())
//                  .registerTypeAdapterFactory(new GsonAdaptersMultiRequestElement())
//                  .registerTypeAdapterFactory(new GsonAdaptersMultiResponseBody())
//                  .registerTypeAdapterFactory(new GsonAdaptersMultiResponseElement())
//                  .registerTypeAdapterFactory(new GsonAdaptersMultiErrorResponse())
                  .create()),
            nxtIdConfigsProvider);
   }

}
