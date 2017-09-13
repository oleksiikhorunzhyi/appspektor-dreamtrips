package com.worldventures.dreamtrips.core.module;

import android.app.Application;
import android.content.Context;

import com.messenger.di.MessengerModule;
import com.techery.spares.application.BaseApplicationWithInjector;
import com.techery.spares.module.DebugModule;
import com.techery.spares.module.InjectingApplicationModule;
import com.techery.spares.module.qualifier.ForApplication;
import com.worldventures.dreamtrips.App;
import com.worldventures.dreamtrips.core.janet.JanetModule;
import com.worldventures.dreamtrips.core.janet.JanetUploaderyModule;
import com.worldventures.dreamtrips.core.janet.cache.CacheActionStorageModule;
import com.worldventures.dreamtrips.core.repository.SnappyModule;
import com.worldventures.dreamtrips.modules.common.ResponseSnifferModule;
import com.worldventures.dreamtrips.modules.common.SessionProcessingModule;
import com.worldventures.dreamtrips.modules.dtl_flow.di.DtlModule;
import com.worldventures.dreamtrips.modules.facebook.FacebookAppModule;
import com.worldventures.dreamtrips.modules.gcm.ActionReceiverModule;
import com.worldventures.dreamtrips.modules.gcm.GcmModule;
import com.worldventures.dreamtrips.modules.mapping.MappingModule;
import com.worldventures.dreamtrips.modules.media_picker.OldMediaPickerModule;
import com.worldventures.dreamtrips.social.di.SocialAppModule;
import com.worldventures.dreamtrips.wallet.di.SmartCardModule;

import dagger.Module;
import dagger.Provides;

@Module(
      injects = {App.class},
      includes = {
            // base injection and helpers/drivers
            InjectingApplicationModule.class,
            //
            DebugModule.class,
            InitializerModule.class,
            HolderModule.class,
            SnappyModule.class,
            ManagerModule.class,
            //
            ApiModule.class,
            AmazonModule.class,
            //
            RouteCreatorModule.class,
            //
            CacheActionStorageModule.class,
            //
            GcmModule.class,
            ActionReceiverModule.class,
            //
            ResponseSnifferModule.class,
            BadgeCountObserverModule.class,
            //
            NavigationModule.class,
            //
            LocaleModule.class,
            AppVersionNameModule.class,
            //
            MessengerModule.class,
            DtlModule.class,
            //
            JanetModule.class,
            JanetUploaderyModule.class,
            AnalyticsModule.class,
            SessionProcessingModule.class,
            //
            MappingModule.class,
            //
            SmartCardModule.class,
            //
            DeviceModule.class,
            FacebookAppModule.class,
            OldMediaPickerModule.class,
            SocialAppModule.class,
      },
      library = true,
      complete = false,
      overrides = true)
public class AppModule {

   protected App app;

   public AppModule(App app) {
      this.app = app;
   }

   @Provides
   Application provideApplication() {
      return app;
   }

   @Provides
   BaseApplicationWithInjector appWithInjector() {
      return app;
   }

   @ForApplication
   @Provides
   Context provideAppContext() {
      return app.getApplicationContext();
   }

   @Provides
   Context provideContext() {
      return app.getApplicationContext();
   }
}
