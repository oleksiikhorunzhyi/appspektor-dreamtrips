package com.worldventures.dreamtrips.wallet.analytics;

import com.worldventures.dreamtrips.core.utils.tracksystem.AdobeTracker;
import com.worldventures.dreamtrips.core.utils.tracksystem.AnalyticsEvent;
import com.worldventures.dreamtrips.core.utils.tracksystem.Attribute;

@AnalyticsEvent(action = "dta:wallet:SmartCard Update:Step 4:Installing Update:Fail",
                trackers = AdobeTracker.TRACKER_KEY)
public class RetryInstallUpdateAction extends WalletAnalyticsAction {

   @Attribute("updateretry") final String retry;
   @Attribute("currentversion") final String currentVersion;
   @Attribute("latestversion") final String latestVersion;

   public RetryInstallUpdateAction(String currentVersion, String latestVersion, boolean retry) {
      this.currentVersion = currentVersion;
      this.latestVersion = latestVersion;
      this.retry = retry? "1" : "0";
   }
}
