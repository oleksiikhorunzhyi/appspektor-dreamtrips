package com.worldventures.dreamtrips.wallet.analytics;

import com.worldventures.dreamtrips.core.utils.tracksystem.AdobeTracker;
import com.worldventures.dreamtrips.core.utils.tracksystem.AnalyticsEvent;
import com.worldventures.dreamtrips.core.utils.tracksystem.Attribute;

@AnalyticsEvent(action = "wallet:setup:Step 1:Card Successfully Entered",
                trackers = AdobeTracker.TRACKER_KEY)
public class ScidScannedAction extends WalletAnalyticsAction {

   @Attribute("cardscansuccess") final String success = "1";
   @Attribute("cardsetupstep2") final String step = "1";
   @Attribute("cardinputmethod") final String method = "Scan";

   public ScidScannedAction(String cid) {
      this.cid = cid;
   }
}
