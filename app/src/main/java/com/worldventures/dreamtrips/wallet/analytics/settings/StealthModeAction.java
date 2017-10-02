package com.worldventures.dreamtrips.wallet.analytics.settings;

import com.worldventures.core.service.analytics.AnalyticsEvent;
import com.worldventures.core.service.analytics.Attribute;
import com.worldventures.core.service.analytics.AdobeTracker;
import com.worldventures.dreamtrips.wallet.analytics.WalletAnalyticsAction;

@AnalyticsEvent(action = "wallet:settings:privacy",
                trackers = AdobeTracker.TRACKER_KEY)
public class StealthModeAction extends WalletAnalyticsAction {

   @Attribute("privacystatus") final String privacyStatus;
   @Attribute("privacychange") String privacyChange = "1";

   public StealthModeAction(boolean stealthModeEnabled) {
      this.privacyStatus = stealthModeEnabled ? "Private" : "Not Private";
   }
}
