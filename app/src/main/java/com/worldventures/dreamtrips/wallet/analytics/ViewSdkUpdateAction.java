package com.worldventures.dreamtrips.wallet.analytics;

import com.worldventures.dreamtrips.core.utils.tracksystem.AdobeTracker;
import com.worldventures.dreamtrips.core.utils.tracksystem.AnalyticsEvent;
import com.worldventures.dreamtrips.core.utils.tracksystem.Attribute;

@AnalyticsEvent(action = "dta:wallet:SmartCard Update:Step 1",
                trackers = AdobeTracker.TRACKER_KEY)
public class ViewSdkUpdateAction extends WalletAnalyticsAction {

   @Attribute("scupdatestep1") final String updateStep = "1";
   @Attribute("currentversion") final String currentVersion;
   @Attribute("latestversion") final String latestVersion;
   @Attribute("dtupdaterqrd") final String updateRequired;

   public ViewSdkUpdateAction(String latestVersion, String currentVersion, boolean updateRequired) {
      this.latestVersion = latestVersion;
      this.currentVersion = currentVersion;
      this.updateRequired = updateRequired ? "Yes" : "No";
   }
}
