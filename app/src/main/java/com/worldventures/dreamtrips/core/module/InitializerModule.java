package com.worldventures.dreamtrips.core.module;

import android.content.Context;

import com.messenger.di.MessengerInitializerModule;
import com.worldventures.core.di.AppInitializer;
import com.worldventures.dreamtrips.core.initializer.AnalyticsInitializer;
import com.worldventures.dreamtrips.core.initializer.BadgeCountObserverInitializer;
import com.worldventures.dreamtrips.core.initializer.FabricInitializer;
import com.worldventures.dreamtrips.core.initializer.FacebookInitializer;
import com.worldventures.dreamtrips.core.initializer.FrescoInitializer;
import com.worldventures.dreamtrips.core.initializer.JodaTimeInitializer;
import com.worldventures.dreamtrips.core.initializer.LeakCanaryInitializer;
import com.worldventures.dreamtrips.core.initializer.LoggingInitializer;
import com.worldventures.dreamtrips.core.initializer.NewrelicInitializer;
import com.worldventures.dreamtrips.core.initializer.SoftInputInitializer;
import com.worldventures.dreamtrips.core.initializer.AppConfigurationInitializer;
import com.worldventures.dreamtrips.core.initializer.TwitterInitializer;
import com.worldventures.dreamtrips.core.initializer.ViewServerInitializer;
import com.worldventures.dreamtrips.modules.config.service.AppConfigurationInteractor;
import com.worldventures.dreamtrips.modules.dtl.service.DtlLocationInteractor;
import com.worldventures.dreamtrips.modules.dtl.service.FilterDataInteractor;
import com.worldventures.dreamtrips.modules.dtl.service.Initializable;

import dagger.Module;
import dagger.Provides;

@Module(
      injects = {
            LeakCanaryInitializer.class,
            FabricInitializer.class,
            NewrelicInitializer.class,
            FrescoInitializer.class,
            SoftInputInitializer.class,
            ViewServerInitializer.class,
            BadgeCountObserverInitializer.class,
            JodaTimeInitializer.class,
            AnalyticsInitializer.class,
            FacebookInitializer.class,
            TwitterInitializer.class,
      },
      includes = {
            MessengerInitializerModule.class,
      },
      library = true, complete = false)
public class InitializerModule {

   @Provides(type = Provides.Type.SET)
   AppInitializer provideEmptyInitializer() {
      return injector -> {
         //nothing to do here
      };
   }

   @Provides(type = Provides.Type.SET)
   AppInitializer provideAnalyticsInitializer() {
      return new AnalyticsInitializer();
   }

   @Provides(type = Provides.Type.SET)
   AppInitializer provideJodaInitializer(Context context) {
      return new JodaTimeInitializer(context);
   }

   @Provides(type = Provides.Type.SET)
   AppInitializer provideSoftInputInitializer() {
      return new SoftInputInitializer();
   }

   @Provides(type = Provides.Type.SET)
   public AppInitializer provideLeakCanaryInitializer() {
      return new LeakCanaryInitializer();
   }

   @Provides(type = Provides.Type.SET)
   public AppInitializer provideLoggingInitializer() {
      return new LoggingInitializer();
   }

   @Provides(type = Provides.Type.SET)
   public AppInitializer provideViewServerInitializer() {
      return new ViewServerInitializer();
   }

   @Provides(type = Provides.Type.SET)
   public AppInitializer provideFabricInitializer() {
      return new FabricInitializer();
   }

   @Provides(type = Provides.Type.SET)
   public AppInitializer provideNewrelicInitializer() {
      return new NewrelicInitializer();
   }

   @Provides(type = Provides.Type.SET)
   public AppInitializer provideFrescoInitializer() {
      return new FrescoInitializer();
   }

   @Provides(type = Provides.Type.SET)
   public AppInitializer provideBadgeCountObserverInitializer() {
      return new BadgeCountObserverInitializer();
   }

   @Provides(type = Provides.Type.SET)
   public AppInitializer provideFacebookInitializer() {
      return new FacebookInitializer();
   }

   @Provides(type = Provides.Type.SET)
   public Initializable provideDtlLocationInteractor(DtlLocationInteractor dtlLocationInteractor) {
      return dtlLocationInteractor;
   }

   @Provides(type = Provides.Type.SET)
   public Initializable provideDtlFilterDataInteractor(FilterDataInteractor filterDataInteractor) {
      return filterDataInteractor;
   }

   @Provides(type = Provides.Type.SET)
   public AppInitializer provideVersionCheckInitializer(AppConfigurationInteractor interactor) {
      return new AppConfigurationInitializer(interactor);
   }

   @Provides(type = Provides.Type.SET)
   public AppInitializer provideTwitterInitializer() {
      return new TwitterInitializer();
   }

}
