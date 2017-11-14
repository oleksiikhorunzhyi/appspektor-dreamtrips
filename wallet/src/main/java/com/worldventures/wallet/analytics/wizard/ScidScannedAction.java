package com.worldventures.wallet.analytics.wizard;

import com.worldventures.core.service.analytics.AdobeTracker;
import com.worldventures.core.service.analytics.Attribute;
import com.worldventures.janet.analytics.AnalyticsEvent;
import com.worldventures.wallet.analytics.WalletAnalyticsAction;

@AnalyticsEvent(action = "wallet:setup:Step 1:Card Successfully Entered",
                trackers = AdobeTracker.TRACKER_KEY)
public class ScidScannedAction extends WalletAnalyticsAction {

   @Attribute("cardscansuccess") final String success = "1";
   @Attribute("cardsetupstep2") final String step = "1";
   @Attribute("cardinputmethod") final String method = "Scan";

   public ScidScannedAction(String cid) {
      super(cid);
   }
}
