package com.worldventures.dreamtrips.modules.version_check;

import android.app.Activity;

import com.worldventures.dreamtrips.core.repository.SnappyRepository;
import com.worldventures.dreamtrips.modules.common.delegate.system.AppInfoProvider;
import com.worldventures.dreamtrips.modules.version_check.delegate.VersionUpdateDelegate;
import com.worldventures.dreamtrips.modules.version_check.delegate.VersionUpdateUiDelegate;
import com.worldventures.dreamtrips.modules.version_check.delegate.VersionUpdateUiDelegateImpl;
import com.worldventures.dreamtrips.modules.version_check.util.VersionComparator;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;

@Module (complete = false, library = true)
public class VersionCheckActivityModule {

   @Provides
   @Singleton
   VersionUpdateDelegate provideVersionUpdateDelegate(SnappyRepository snappyRepository,
         VersionComparator versionComparator, VersionUpdateUiDelegate versionUpdateUiDelegate,
         AppInfoProvider appInfoProvider) {
      return new VersionUpdateDelegate(snappyRepository, versionComparator, versionUpdateUiDelegate, appInfoProvider);
   }

   @Provides
   @Singleton
   VersionUpdateUiDelegate provideVersionUpdateUiDelegate(Activity activity, SnappyRepository snappyRepository) {
      return new VersionUpdateUiDelegateImpl(activity, snappyRepository);
   }
}
