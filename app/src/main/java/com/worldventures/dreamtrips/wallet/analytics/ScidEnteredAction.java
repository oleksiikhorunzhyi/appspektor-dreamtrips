package com.worldventures.dreamtrips.wallet.analytics;

import com.worldventures.dreamtrips.core.utils.tracksystem.AdobeTracker;
import com.worldventures.dreamtrips.core.utils.tracksystem.AnalyticsEvent;
import com.worldventures.dreamtrips.core.utils.tracksystem.Attribute;

@AnalyticsEvent(action = "wallet:setup:Step 2:Card Successfully Entered",
                trackers = AdobeTracker.TRACKER_KEY)
public class ScidEnteredAction extends WalletAnalyticsAction {

   @Attribute("cardsetupstep2") final String cardSetupStep2 = "1";
   @Attribute("cardinputmethod") final String cardInputMethod = "Manual";

   public ScidEnteredAction(String cid) {
      this.cid = cid;
   }
}
