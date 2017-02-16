package com.worldventures.dreamtrips.wallet.analytics;

import com.worldventures.dreamtrips.core.utils.tracksystem.AdobeTracker;
import com.worldventures.dreamtrips.core.utils.tracksystem.AnalyticsEvent;
import com.worldventures.dreamtrips.core.utils.tracksystem.Attribute;

@AnalyticsEvent(action = "wallet:setup:Step 5:Display Photo Has Been Set",
                trackers = AdobeTracker.TRACKER_KEY)
public class PhotoWasSetAction extends WalletAnalyticsAction {

   @Attribute("displayphotoset") final String displayPhotoSet = "1";
   @Attribute("photomethod") String photoMethod = "Default";
   @Attribute("displayname") final String displayName;
   @Attribute("cardsetupstep5") final String cardSetupStep5 = "1";

   public PhotoWasSetAction(String displayName, String cid) {
      this.displayName = displayName;
      this.cid = cid;
   }
}
