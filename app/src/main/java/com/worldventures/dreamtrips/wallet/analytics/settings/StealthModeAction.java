package com.worldventures.dreamtrips.wallet.analytics.settings;

import com.worldventures.dreamtrips.core.utils.tracksystem.AdobeTracker;
import com.worldventures.dreamtrips.core.utils.tracksystem.AnalyticsEvent;
import com.worldventures.dreamtrips.core.utils.tracksystem.Attribute;
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
