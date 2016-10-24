package com.worldventures.dreamtrips.wallet.analytics;

import com.worldventures.dreamtrips.core.utils.tracksystem.AdobeTracker;
import com.worldventures.dreamtrips.core.utils.tracksystem.AnalyticsEvent;
import com.worldventures.dreamtrips.core.utils.tracksystem.Attribute;

@AnalyticsEvent(action = "wallet:setup:Step 3:Set Touch PIN",
                trackers = AdobeTracker.TRACKER_KEY)
public class SetPinAction extends WalletAnalyticsAction {

   @Attribute("displayname") final String displayname;

   public SetPinAction(String displayname) {
      this.displayname = displayname;
   }
}
