package com.worldventures.dreamtrips.wallet.analytics;

import com.worldventures.dreamtrips.core.utils.tracksystem.AdobeTracker;
import com.worldventures.dreamtrips.core.utils.tracksystem.AnalyticsEvent;
import com.worldventures.dreamtrips.core.utils.tracksystem.Attribute;

@AnalyticsEvent(action = "wallet:setup:Step 1:Card Successfully Entered",
                trackers = AdobeTracker.TRACKER_KEY)
public class ScidEnteredAction extends WalletAnalyticsAction {

   @Attribute("cardsetupstep1") final String cardSetupStep1 = "1";
   @Attribute("cardinputmethod") final String cardInputMethod;

   private ScidEnteredAction(String cardInputMethod, String cid) {
      this.cardInputMethod = cardInputMethod;
      this.cid = cid;
   }

   public static ScidEnteredAction forScan(String cid) {
      return new ScidEnteredAction("Scan", cid);
   }

   public static ScidEnteredAction forManual(String cid) {
      return new ScidEnteredAction("Manual", cid);
   }
}
