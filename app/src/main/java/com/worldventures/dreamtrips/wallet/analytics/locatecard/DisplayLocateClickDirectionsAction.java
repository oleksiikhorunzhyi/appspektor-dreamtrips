package com.worldventures.dreamtrips.wallet.analytics.locatecard;

import com.worldventures.dreamtrips.core.utils.tracksystem.AdobeTracker;
import com.worldventures.dreamtrips.core.utils.tracksystem.AnalyticsEvent;
import com.worldventures.dreamtrips.core.utils.tracksystem.Attribute;
import com.worldventures.dreamtrips.wallet.domain.entity.lostcard.WalletCoordinates;

@AnalyticsEvent(action = "wallet:settings:locate smartcard:display location:get directions",
                trackers = AdobeTracker.TRACKER_KEY)
public class DisplayLocateClickDirectionsAction extends BaseLocateSmartCardAction {

   @Attribute("getdirections") String getDirections = "1";
   @Attribute("trackingenabled") String trackingEnabled = "Yes";
   @Attribute("locationavailable") String locationAvailable;

   @Override
   public void setLocation(WalletCoordinates walletCoordinates) {
      super.setLocation(walletCoordinates);
      locationAvailable = walletCoordinates != null ? "Yes" : "No";
   }
}
