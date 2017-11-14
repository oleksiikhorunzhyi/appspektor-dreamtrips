package com.worldventures.dreamtrips.modules.config.delegate;


import com.worldventures.core.service.analytics.AnalyticsInteractor;
import com.worldventures.dreamtrips.core.repository.SnappyRepository;
import com.worldventures.dreamtrips.modules.common.delegate.system.AppInfoProvider;
import com.worldventures.dreamtrips.modules.config.model.UpdateRequirement;
import com.worldventures.dreamtrips.modules.config.service.analytics.UpdateAppAction;
import com.worldventures.dreamtrips.modules.config.util.VersionComparator;

import java.util.concurrent.TimeUnit;

import timber.log.Timber;

public class VersionUpdateDelegate {

   public static final long OPTIONAL_DIALOG_MIN_INTERVAL = TimeUnit.HOURS.toMillis(1);

   private final SnappyRepository snappyRepository;
   private final VersionComparator versionComparator;
   private final VersionUpdateUiDelegate versionUpdateUiDelegate;
   private final AppInfoProvider appInfoProvider;
   private final AnalyticsInteractor analyticsInteractor;
   private boolean updateDialogAlreadyShown;

   public VersionUpdateDelegate(SnappyRepository snappyRepository, VersionComparator versionComparator,
         VersionUpdateUiDelegate versionUpdateUiDelegate, AppInfoProvider appInfoProvider,
         AnalyticsInteractor analyticsInteractor) {
      this.snappyRepository = snappyRepository;
      this.versionComparator = versionComparator;
      this.versionUpdateUiDelegate = versionUpdateUiDelegate;
      this.appInfoProvider = appInfoProvider;
      this.analyticsInteractor = analyticsInteractor;
   }

   public void processUpdateRequirement(UpdateRequirement updateRequirement) {
      if (updateDialogAlreadyShown) {
         return;
      }
      try {
         tryProcessUpdateRequirement(updateRequirement);
      } catch (Exception e) {
         Timber.w(e, "Could not process update requirement");
      }
   }

   private void tryProcessUpdateRequirement(UpdateRequirement updateRequirement) throws Exception {
      boolean currentVersionOutdated = versionComparator
            .currentVersionIsOlderThanSuggested(appInfoProvider.getAppVersion(), updateRequirement.getAppVersion());
      if (!currentVersionOutdated) {
         return;
      }

      boolean forceUpdate = System.currentTimeMillis() > updateRequirement.getTimeStamp();
      if (!forceUpdate) {
         long timestampSinceLastShown = snappyRepository.getAppUpdateOptionalDialogConfirmedTimestamp();
         if (timestampSinceLastShown != 0) {
            long timeSinceShownLast = System.currentTimeMillis() - timestampSinceLastShown;
            if (timeSinceShownLast < OPTIONAL_DIALOG_MIN_INTERVAL) {
               return;
            }
         }
      }
      showUpdateUpdateDialog(forceUpdate, updateRequirement);
   }

   private void showUpdateUpdateDialog(boolean forceUpdate, UpdateRequirement updateRequirement) {
      if (forceUpdate) {
         versionUpdateUiDelegate.showForceUpdateDialog();
      } else {
         versionUpdateUiDelegate.showOptionalUpdateDialog(updateRequirement.getTimeStamp());
      }
      updateDialogAlreadyShown = true;
      analyticsInteractor.analyticsActionPipe().send(new UpdateAppAction(updateRequirement.getAppVersion()));
   }
}
