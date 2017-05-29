package com.worldventures.dreamtrips.wallet.analytics.firmware.action;

import android.support.annotation.NonNull;

import com.worldventures.dreamtrips.api.smart_card.firmware.model.FirmwareInfo;
import com.worldventures.dreamtrips.core.utils.tracksystem.AdobeTracker;
import com.worldventures.dreamtrips.core.utils.tracksystem.AnalyticsEvent;
import com.worldventures.dreamtrips.core.utils.tracksystem.Attribute;
import com.worldventures.dreamtrips.wallet.domain.entity.FirmwareUpdateData;

@AnalyticsEvent(action = "wallet:SmartCard Update:Step 1",
                navigationState = true,
                trackers = AdobeTracker.TRACKER_KEY)
public class ViewSdkUpdateAction extends FirmwareAnalyticsAction {

   @Attribute("scupdatestep1") final String updateStep = "1";
   @Attribute("currentversion") String currentVersion;
   @Attribute("latestversion") String latestVersion;
   @Attribute("dtupdaterqrd") String updateRequired;

   @Override
   public void setFirmwareData(@NonNull FirmwareUpdateData data) {
      super.setFirmwareData(data);
      FirmwareInfo info = data.firmwareInfo();
      if (info != null) {
         this.latestVersion = info.firmwareVersion();
         this.updateRequired = info.isCompatible() ? "Yes" : "No";
      }
      this.currentVersion = data.currentFirmwareVersion().nordicAppVersion();
   }
}
