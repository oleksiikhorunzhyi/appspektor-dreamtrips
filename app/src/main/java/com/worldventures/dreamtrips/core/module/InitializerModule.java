package com.worldventures.dreamtrips.core.module;

import android.content.Context;

import com.messenger.di.MessengerInitializerModule;
import com.techery.spares.application.AppInitializer;
import com.worldventures.dreamtrips.core.initializer.AnalyticsInitializer;
import com.worldventures.dreamtrips.core.initializer.BadgeCountObserverInitializer;
import com.worldventures.dreamtrips.core.initializer.CachedEntityCommandInitializer;
import com.worldventures.dreamtrips.core.initializer.FabricInitializer;
import com.worldventures.dreamtrips.core.initializer.FacebookInitializer;
import com.worldventures.dreamtrips.core.initializer.FrescoInitializer;
import com.worldventures.dreamtrips.core.initializer.JodaTimeInitializer;
import com.worldventures.dreamtrips.core.initializer.LeakCanaryInitializer;
import com.worldventures.dreamtrips.core.initializer.LoggingInitializer;
import com.worldventures.dreamtrips.core.initializer.NewrelicInitializer;
import com.worldventures.dreamtrips.core.initializer.RxJavaLoggingInitializer;
import com.worldventures.dreamtrips.core.initializer.SnappyStorageManagerInitializer;
import com.worldventures.dreamtrips.core.initializer.SoftInputInitializer;
import com.worldventures.dreamtrips.core.initializer.VersionCheckInitializer;
import com.worldventures.dreamtrips.core.initializer.ViewServerInitializer;
import com.worldventures.dreamtrips.modules.common.delegate.CachedEntityInteractor;
import com.worldventures.dreamtrips.modules.dtl.service.DtlLocationInteractor;
import com.worldventures.dreamtrips.modules.dtl.service.FilterDataInteractor;
import com.worldventures.dreamtrips.modules.dtl.service.Initializable;
import com.worldventures.dreamtrips.modules.version_check.service.VersionCheckInteractor;

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
            SnappyStorageManagerInitializer.class,
            FacebookInitializer.class,
      },
      includes = {
            MessengerInitializerModule.class
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
   AppInitializer provideSnappyStorageManagerInitializer() {
      return new SnappyStorageManagerInitializer();
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
   public AppInitializer provideRxLogInitializer() {
      return new RxJavaLoggingInitializer();
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
   public AppInitializer provideCachedEntitiesInitializer(CachedEntityInteractor interactor) {
      return new CachedEntityCommandInitializer(interactor);
   }

   @Provides(type = Provides.Type.SET)
   public AppInitializer provideFacebookInitializer() {
      return new FacebookInitializer();
   }

   @Provides(type = Provides.Type.SET)
   public AppInitializer provideVersionCheckInitializer(VersionCheckInteractor interactor) {
      return new VersionCheckInitializer(interactor);
   }

   @Provides(type = Provides.Type.SET)
   public Initializable provideDtlLocationInteractor(DtlLocationInteractor dtlLocationInteractor) {
      return dtlLocationInteractor;
   }

   @Provides(type = Provides.Type.SET)
   public Initializable provideDtlFilterDataInteractor(FilterDataInteractor filterDataInteractor) {
      return filterDataInteractor;
   }
}
