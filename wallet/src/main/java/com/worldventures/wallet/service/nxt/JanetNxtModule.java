package com.worldventures.wallet.service.nxt;

import com.worldventures.core.utils.AppVersionNameBuilder;
import com.worldventures.dreamtrips.api.api_common.converter.GsonProvider;
import com.worldventures.wallet.domain.session.NxtSessionHolder;
import com.worldventures.wallet.service.WalletSocialInfoProvider;
import com.worldventures.wallet.util.WalletBuildConfigHelper;

import java.util.concurrent.TimeUnit;

import javax.inject.Named;
import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;
import io.techery.janet.ActionService;
import io.techery.janet.Janet;
import io.techery.janet.gson.GsonConverter;
import io.techery.janet.okhttp3.OkClient;
import io.techery.mappery.MapperyContext;
import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;

@Module(complete = false, library = true)
public class JanetNxtModule {

   public static final String JANET_NXT = "JANET_NXT";

   @Singleton
   @Provides
   @Named(JANET_NXT)
   Janet provideJanet(@Named(JANET_NXT) ActionService service) {
      return new Janet.Builder().addService(service).build();
   }

   @Provides
   @Named(JANET_NXT)
   OkHttpClient provideJanetOkHttp3Client(NxtIdConfigsProvider nxtIdConfigsProvider, WalletBuildConfigHelper configHelper) {
      final OkHttpClient.Builder builder = new OkHttpClient.Builder();
      if (configHelper.isDebug()) {
         builder.addInterceptor(provideOkHttp3Interceptor());
      }
      builder
            .connectTimeout(nxtIdConfigsProvider.apiTimeoutSec(), TimeUnit.SECONDS)
            .readTimeout(nxtIdConfigsProvider.apiTimeoutSec(), TimeUnit.SECONDS)
            .writeTimeout(nxtIdConfigsProvider.apiTimeoutSec(), TimeUnit.SECONDS);
      return builder.build();
   }

   private Interceptor provideOkHttp3Interceptor() {
      return new HttpLoggingInterceptor().setLevel(HttpLoggingInterceptor.Level.BASIC);
   }

   @Provides
   @Named(JANET_NXT)
   ActionService provideNxtActionService(NxtSessionHolder nxtSessionHolder, AppVersionNameBuilder appVersionNameBuilder,
         WalletSocialInfoProvider socialInfoProvider, MapperyContext mapperyContext, NxtIdConfigsProvider nxtIdConfigsProvider,
         @Named(JANET_NXT) OkHttpClient httpClient) {
      return new NxtHttpService(nxtSessionHolder, appVersionNameBuilder, socialInfoProvider, mapperyContext,
            nxtIdConfigsProvider,
            nxtIdConfigsProvider.nxtidApi(),
            new OkClient(httpClient),
            new GsonConverter(new GsonProvider().provideBuilder().create()));
   }

}
