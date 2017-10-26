package com.worldventures.dreamtrips.core.module;

import android.app.Application;
import android.content.Context;

import com.messenger.di.MessengerModule;
import com.techery.spares.module.DebugModule;
import com.techery.spares.module.InjectingApplicationModule;
import com.worldventures.core.di.CoreModule;
import com.worldventures.core.di.qualifier.ForApplication;
import com.worldventures.core.model.session.SessionHolder;
import com.worldventures.core.modules.facebook.FacebookAppModule;
import com.worldventures.core.modules.infopages.ImmutableStaticPageProviderConfig;
import com.worldventures.core.modules.infopages.StaticPageProviderConfig;
import com.worldventures.core.service.DeviceInfoProvider;
import com.worldventures.dreamtrips.App;
import com.worldventures.dreamtrips.BuildConfig;
import com.worldventures.dreamtrips.core.janet.JanetModule;
import com.worldventures.dreamtrips.core.janet.JanetUploaderyModule;
import com.worldventures.dreamtrips.core.janet.cache.CacheActionStorageModule;
import com.worldventures.dreamtrips.core.repository.SnappyModule;
import com.worldventures.dreamtrips.modules.common.ResponseSnifferModule;
import com.worldventures.dreamtrips.modules.dtl_flow.di.DtlModule;
import com.worldventures.dreamtrips.modules.gcm.ActionReceiverModule;
import com.worldventures.dreamtrips.modules.gcm.GcmModule;
import com.worldventures.dreamtrips.modules.mapping.MappingModule;
import com.worldventures.dreamtrips.modules.media_picker.OldMediaPickerModule;
import com.worldventures.dreamtrips.social.di.SocialAppModule;
import com.worldventures.dreamtrips.wallet.WalletDtAppModule;

import dagger.Module;
import dagger.Provides;

@Module(
      injects = {App.class},
      includes = {
            CoreModule.class,
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
            //
            MappingModule.class,
            //
            WalletDtAppModule.class,
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

   /**
    * Use Context without ForApplication qualifier.
    */
   @ForApplication
   @Provides
   @Deprecated
   Context provideAppContext() {
      return app.getApplicationContext();
   }

   @Provides
   Context provideContext() {
      return app.getApplicationContext();
   }

   @Provides
   StaticPageProviderConfig provideConfig(SessionHolder appSessionHolder, DeviceInfoProvider deviceInfoProvider) {
      return ImmutableStaticPageProviderConfig.builder()
            .appSessionHolder(appSessionHolder)
            .deviceInfoProvider(deviceInfoProvider)
            .apiUrl(BuildConfig.DreamTripsApi)
            .backofficeUrl(BuildConfig.BACKOFFICE_URL)
            .uploaderyUrl(BuildConfig.UPLOADERY_API_URL)
            .forgotPasswordUrl(BuildConfig.FORGOT_PASSWORD_URL)
            .build();
   }
}
