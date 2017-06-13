package com.worldventures.dreamtrips.wallet.analytics.firmware.action;

import android.support.annotation.NonNull;

import com.worldventures.dreamtrips.core.utils.tracksystem.AdobeTracker;
import com.worldventures.dreamtrips.core.utils.tracksystem.AnalyticsEvent;
import com.worldventures.dreamtrips.core.utils.tracksystem.Attribute;
import com.worldventures.dreamtrips.wallet.domain.entity.FirmwareUpdateData;

@AnalyticsEvent(action = "wallet:settings:general:SmartCard Update:Step 4:Installing Update:Fail",
                trackers = AdobeTracker.TRACKER_KEY)
public class RetryInstallUpdateAction extends FirmwareAnalyticsAction {

   @Attribute("updateretry") final String retry;
   @Attribute("currentversion") String currentVersion;
   @Attribute("latestversion") String latestVersion;

   public RetryInstallUpdateAction(boolean retry) {
      this.retry = retry ? "1" : "0";
   }

   @Override
   public void setFirmwareData(@NonNull FirmwareUpdateData data) {
      super.setFirmwareData(data);
      this.currentVersion = data.currentFirmwareVersion().nordicAppVersion();
      this.latestVersion = data.firmwareInfo().firmwareVersion();
   }
}
