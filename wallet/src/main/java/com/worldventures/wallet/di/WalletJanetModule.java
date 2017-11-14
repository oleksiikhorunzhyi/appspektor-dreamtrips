package com.worldventures.wallet.di;

import android.content.Context;

import com.worldventures.core.janet.ActionServiceLoggerImpl;
import com.worldventures.core.janet.CommandInjectorImpl;
import com.worldventures.core.janet.Injector;
import com.worldventures.core.janet.SessionActionPipeCreator;
import com.worldventures.core.janet.TimberServiceWrapper;
import com.worldventures.core.janet.cache.CacheResultWrapper;
import com.worldventures.core.janet.cache.storage.ActionStorage;
import com.worldventures.core.service.analytics.AnalyticsService;
import com.worldventures.dreamtrips.mobilesdk.DreamtripsApiProvider;
import com.worldventures.janet.injection.CommandInjector;
import com.worldventures.janet.injection.DaggerActionServiceWrapper;
import com.worldventures.wallet.service.SmartCardErrorServiceWrapper;
import com.worldventures.wallet.service.WalletAnalyticsServiceWrapper;
import com.worldventures.wallet.util.TimberLogger;

import java.util.Set;
import java.util.concurrent.TimeUnit;

import javax.inject.Named;
import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;
import io.techery.janet.ActionService;
import io.techery.janet.CommandActionService;
import io.techery.janet.Janet;
import io.techery.janet.SmartCardActionService;
import io.techery.janet.smartcard.client.SmartCardClient;

@Module(includes = {
      WalletActionStorageModule.class,
      WalletApiTypeAdapterModule.class,
}, library = true, complete = false)
public class WalletJanetModule {

   public static final String JANET_WALLET = "JANET_WALLET";

   private ActionService createSmartCardService(SmartCardClient client) {
      return new TimberServiceWrapper(
            new SmartCardActionService.Builder(client)
                  .addDefaults()
                  .setLogger(new TimberLogger("SC_ABS_LAYER"))
                  .setResponseTimeout(TimeUnit.MINUTES.toMillis(2L))
                  .build()
      );
   }

   @Provides
   @Singleton
   SmartCardErrorServiceWrapper provideSmartCardErrorServiceWrapper(SmartCardClient client) {
      return new SmartCardErrorServiceWrapper(createSmartCardService(client));
   }

   @Provides(type = Provides.Type.SET)
   @Named(JANET_WALLET)
   ActionService provideSmartCardService(SmartCardErrorServiceWrapper serviceWrapper) {
      return serviceWrapper;
   }


   @Provides(type = Provides.Type.SET)
   @Named(JANET_WALLET)
   ActionService provideCommandActionService(Context context, @Named(JANET_WALLET) Set<ActionStorage> cacheStorageSet) {
      final CommandInjector injector = new CommandInjectorImpl(((Injector) context)
            .getObjectGraph().plus(new WalletCommandModule()));

      final CacheResultWrapper service = new CacheResultWrapper(
            new DaggerActionServiceWrapper(
                  new TimberServiceWrapper(new CommandActionService()), injector,
                  new ActionServiceLoggerImpl()));

      for (ActionStorage actionStorage : cacheStorageSet) {
         service.bindStorage(actionStorage.getActionClass(), actionStorage);
      }
      return service;
   }


   @Provides(type = Provides.Type.SET)
   @Named(JANET_WALLET)
   ActionService provideApiService(DreamtripsApiProvider provider) {
      return provider.createApiService();
   }

   @Singleton
   @Provides
   WalletAnalyticsServiceWrapper provideWalletAnalyticsServiceWrapper(AnalyticsService service) {
      return new WalletAnalyticsServiceWrapper(new TimberServiceWrapper(service));
   }

   @Named(JANET_WALLET)
   @Provides(type = Provides.Type.SET)
   ActionService provideAnalyticsService(WalletAnalyticsServiceWrapper serviceWrapper) {
      return serviceWrapper;
   }

   @Singleton
   @Provides
   @Named(JANET_WALLET)
   Janet provideWalletJanet(@Named(JANET_WALLET) Set<ActionService> services) {
      final Janet.Builder builder = new Janet.Builder();
      for (ActionService service : services) {
         builder.addService(service);
      }
      return builder.build();
   }

   @Named(JANET_WALLET)
   @Singleton
   @Provides
   SessionActionPipeCreator pipeCreator(@Named(JANET_WALLET) Janet janet) {
      return new SessionActionPipeCreator(janet);
   }
}
