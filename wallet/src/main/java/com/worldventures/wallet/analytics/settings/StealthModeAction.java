package com.worldventures.wallet.analytics.settings;

import com.worldventures.janet.analytics.AnalyticsEvent;
import com.worldventures.core.service.analytics.Attribute;
import com.worldventures.core.service.analytics.AdobeTracker;
import com.worldventures.wallet.analytics.WalletAnalyticsAction;

@AnalyticsEvent(action = "wallet:settings:privacy",
                trackers = AdobeTracker.TRACKER_KEY)
public class StealthModeAction extends WalletAnalyticsAction {

   @Attribute("privacystatus") final String privacyStatus;
   @Attribute("privacychange") String privacyChange = "1";

   public StealthModeAction(boolean stealthModeEnabled) {
      this.privacyStatus = stealthModeEnabled ? "Private" : "Not Private";
   }
}
