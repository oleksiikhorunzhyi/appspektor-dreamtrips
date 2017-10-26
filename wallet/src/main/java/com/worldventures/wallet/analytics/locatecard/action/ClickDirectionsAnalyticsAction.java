package com.worldventures.wallet.analytics.locatecard.action;

import com.worldventures.janet.analytics.AnalyticsEvent;
import com.worldventures.core.service.analytics.Attribute;
import com.worldventures.core.service.analytics.AdobeTracker;
import com.worldventures.wallet.domain.entity.lostcard.WalletCoordinates;

@AnalyticsEvent(action = "wallet:settings:locate smartcard:display location:get directions",
                trackers = AdobeTracker.TRACKER_KEY)
public class ClickDirectionsAnalyticsAction extends BaseLocateSmartCardAction {

   @Attribute("getdirections") String getDirections = "1";
   @Attribute("trackingenabled") String trackingEnabled = "Yes";
   @Attribute("locationavailable") String locationAvailable;

   @Override
   public void setLocation(WalletCoordinates walletCoordinates) {
      super.setLocation(walletCoordinates);
      locationAvailable = walletCoordinates != null ? "Yes" : "No";
   }
}
