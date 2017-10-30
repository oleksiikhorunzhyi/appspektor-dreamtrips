package com.worldventures.dreamtrips.core.janet.api_lib;

import android.os.Build;

import com.worldventures.core.utils.AppVersionNameBuilder;
import com.worldventures.core.utils.LocaleHelper;
import com.worldventures.dreamtrips.BuildConfig;
import com.worldventures.dreamtrips.api.api_common.service.ImmutableMonolithConfigData;
import com.worldventures.dreamtrips.api.api_common.service.MonolithConfigData;
import com.worldventures.dreamtrips.mobilesdk.config.ConfigDataProvider;

import java.util.Locale;

public class MonolithConfigDataProvider implements ConfigDataProvider<MonolithConfigData> {

   private final AppVersionNameBuilder appVersionNameBuilder;

   public MonolithConfigDataProvider(AppVersionNameBuilder appVersionNameBuilder) {
      this.appVersionNameBuilder = appVersionNameBuilder;
   }

   @Override
   public MonolithConfigData data() {
      return ImmutableMonolithConfigData.builder()
            .apiUploaderyUrl(BuildConfig.UPLOADERY_API_URL)
            .apiVersion(BuildConfig.API_VERSION)
            .appLanguage(LocaleHelper.getDefaultLocaleFormatted())
            .appPlatform(String.format(Locale.US, "android-%d", Build.VERSION.SDK_INT))
            .appVersion(appVersionNameBuilder.getSemanticVersionName())
            .url(BuildConfig.DreamTripsApi)
            .build();
   }
}
