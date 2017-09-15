package com.techery.spares.module;

import com.techery.spares.session.SessionHolder;
import com.worldventures.dreamtrips.BuildConfig;
import com.worldventures.dreamtrips.modules.common.delegate.system.DeviceInfoProvider;
import com.worldventures.dreamtrips.social.ui.infopages.ImmutableStaticPageProviderConfig;
import com.worldventures.dreamtrips.social.ui.infopages.StaticPageProvider;
import com.worldventures.dreamtrips.social.ui.infopages.StaticPageProviderConfig;

import dagger.Module;
import dagger.Provides;

@Module(library = true, complete = false)
public class SupportModule {

   @Provides
   StaticPageProvider provideStaticPageProvider(StaticPageProviderConfig config) {
      return new StaticPageProvider(config);
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
