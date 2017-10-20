package com.worldventures.dreamtrips.wallet.analytics.wizard;

import com.worldventures.core.service.analytics.AdobeTracker;
import com.worldventures.core.service.analytics.AnalyticsEvent;
import com.worldventures.core.service.analytics.Attribute;
import com.worldventures.dreamtrips.wallet.analytics.WalletAnalyticsAction;

@AnalyticsEvent(action = "wallet:setup:Step 1:Card Successfully Entered",
                trackers = AdobeTracker.TRACKER_KEY)
public class ScidEnteredAction extends WalletAnalyticsAction {

   @Attribute("cardmaninput") final String success = "1";
   @Attribute("cardsetupstep2") final String step = "1";
   @Attribute("cardinputmethod") final String method = "Manual";

   public ScidEnteredAction(String cid) {
      super(cid);
   }
}
