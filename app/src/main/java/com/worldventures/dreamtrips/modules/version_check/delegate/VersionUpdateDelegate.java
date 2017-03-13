package com.worldventures.dreamtrips.modules.version_check.delegate;


import com.worldventures.dreamtrips.core.repository.SnappyRepository;
import com.worldventures.dreamtrips.modules.common.delegate.system.AppInfoProvider;
import com.worldventures.dreamtrips.modules.version_check.model.UpdateRequirement;
import com.worldventures.dreamtrips.modules.version_check.util.VersionComparator;

import java.util.concurrent.TimeUnit;

import timber.log.Timber;

public class VersionUpdateDelegate {

   public static final long OPTIONAL_DIALOG_MIN_INTERVAL = TimeUnit.HOURS.toMillis(1);

   private SnappyRepository snappyRepository;
   private VersionComparator versionComparator;
   private VersionUpdateUiDelegate versionUpdateUiDelegate;
   private AppInfoProvider appInfoProvider;

   private long optionalDialogMinInterval = OPTIONAL_DIALOG_MIN_INTERVAL;
   private boolean updateDialogAlreadyShown;

   public VersionUpdateDelegate(SnappyRepository snappyRepository, VersionComparator versionComparator,
         VersionUpdateUiDelegate versionUpdateUiDelegate, AppInfoProvider appInfoProvider) {
      this.snappyRepository = snappyRepository;
      this.versionComparator = versionComparator;
      this.versionUpdateUiDelegate = versionUpdateUiDelegate;
      this.appInfoProvider = appInfoProvider;
   }

   public void processUpdateRequirement(UpdateRequirement updateRequirement) {
      if (updateDialogAlreadyShown) return;
      try {
         tryProcessUpdateRequirement(updateRequirement);
      } catch (Exception e) {
         Timber.w(e, "Could not process update requirement");
      }
   }

   private void tryProcessUpdateRequirement(UpdateRequirement updateRequirement) throws Exception {
      boolean currentVersionOutdated = versionComparator
            .currentVersionIsOlderThanSuggested(appInfoProvider.getAppVersion(), updateRequirement.getAppVersion());
      if (!currentVersionOutdated) return;

      boolean forceUpdate = System.currentTimeMillis() > updateRequirement.getTimeStamp();
      if (!forceUpdate) {
         long timestampSinceLastShown = snappyRepository.getAppUpdateOptionalDialogConfirmedTimestamp();
         if (timestampSinceLastShown != 0) {
            long timeSinceShownLast = System.currentTimeMillis() - timestampSinceLastShown;
            if (timeSinceShownLast < optionalDialogMinInterval) return;
         }
      }
      showUpdateUpdateDialog(forceUpdate, updateRequirement.getTimeStamp());
   }

   private void showUpdateUpdateDialog(boolean forceUpdate, long updateByTimestamp) {
      if (forceUpdate) {
         versionUpdateUiDelegate.showForceUpdateDialog();
      } else {
         versionUpdateUiDelegate.showOptionalUpdateDialog(updateByTimestamp);
      }
      updateDialogAlreadyShown = true;
   }
}
