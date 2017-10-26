package com.worldventures.wallet.analytics.locatecard.action;

import com.worldventures.janet.analytics.AnalyticsEvent;
import com.worldventures.core.service.analytics.Attribute;
import com.worldventures.core.service.analytics.AdobeTracker;
import com.worldventures.wallet.domain.entity.lostcard.WalletCoordinates;

@AnalyticsEvent(action = "wallet:settings:locate smartcard:display location",
                navigationState = true,
                trackers = AdobeTracker.TRACKER_KEY)
public class DisplayMapAnalyticsAction extends BaseLocateSmartCardAction {

   @Attribute("trackingenabled") final String trackingEnabled = "Yes";
   @Attribute("locationavailable") String locationAvailable = "No";


   @Override
   public void setLocation(WalletCoordinates walletCoordinates) {
      super.setLocation(walletCoordinates);
      locationAvailable = "Yes";
   }
}
