package com.techery.spares.module;

import com.techery.spares.session.SessionHolder;
import com.worldventures.dreamtrips.BuildConfig;
import com.worldventures.dreamtrips.core.session.UserSession;
import com.worldventures.dreamtrips.modules.common.delegate.system.DeviceInfoProvider;
import com.worldventures.dreamtrips.modules.infopages.StaticPageProvider;

import dagger.Module;
import dagger.Provides;

@Module(library = true, complete = false)
public class SupportModule {

   @Provides
   StaticPageProvider provideStaticPageProvider(SessionHolder<UserSession> appSessionHolder, DeviceInfoProvider deviceInfoProvider) {
      return new StaticPageProvider(appSessionHolder, deviceInfoProvider, BuildConfig.DreamTripsApi, BuildConfig.UPLOADERY_API_URL);
   }
}
