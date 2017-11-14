package com.worldventures.wallet.analytics.firmware.action;

import android.support.annotation.NonNull;

import com.worldventures.core.service.analytics.AdobeTracker;
import com.worldventures.core.service.analytics.Attribute;
import com.worldventures.janet.analytics.AnalyticsEvent;
import com.worldventures.wallet.domain.entity.FirmwareUpdateData;

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
