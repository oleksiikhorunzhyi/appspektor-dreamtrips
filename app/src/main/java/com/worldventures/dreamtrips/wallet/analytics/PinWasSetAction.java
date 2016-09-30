package com.worldventures.dreamtrips.wallet.analytics;

import com.worldventures.dreamtrips.core.utils.tracksystem.AdobeTracker;
import com.worldventures.dreamtrips.core.utils.tracksystem.AnalyticsEvent;
import com.worldventures.dreamtrips.core.utils.tracksystem.Attribute;

@AnalyticsEvent(action = "wallet:setup:Step 5:Your PIN is set",
                trackers = AdobeTracker.TRACKER_KEY)
public class PinWasSetAction extends WalletAnalyticsAction {

   @Attribute("displayname") final String displayname;
   @Attribute("cardsetupstep3") final String cardsetupstep3 = "1";

   public PinWasSetAction(String displayname) {
      this.displayname = displayname;
   }
}
