package com.worldventures.dreamtrips.modules.config;

import android.app.Activity;

import com.worldventures.core.service.analytics.AnalyticsInteractor;
import com.worldventures.dreamtrips.core.repository.SnappyRepository;
import com.worldventures.dreamtrips.modules.common.delegate.system.AppInfoProvider;
import com.worldventures.dreamtrips.modules.config.delegate.VersionUpdateDelegate;
import com.worldventures.dreamtrips.modules.config.delegate.VersionUpdateUiDelegate;
import com.worldventures.dreamtrips.modules.config.delegate.VersionUpdateUiDelegateImpl;
import com.worldventures.dreamtrips.modules.config.util.VersionComparator;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;

@Module(complete = false, library = true)
public class ConfigurationActivityModule {

   @Provides
   @Singleton
   VersionUpdateDelegate provideVersionUpdateDelegate(SnappyRepository snappyRepository,
         VersionComparator versionComparator, VersionUpdateUiDelegate versionUpdateUiDelegate,
         AppInfoProvider appInfoProvider, AnalyticsInteractor analyticsInteractor) {
      return new VersionUpdateDelegate(snappyRepository, versionComparator, versionUpdateUiDelegate,
            appInfoProvider, analyticsInteractor);
   }

   @Provides
   @Singleton
   VersionUpdateUiDelegate provideVersionUpdateUiDelegate(Activity activity, SnappyRepository snappyRepository) {
      return new VersionUpdateUiDelegateImpl(activity, snappyRepository);
   }
}
