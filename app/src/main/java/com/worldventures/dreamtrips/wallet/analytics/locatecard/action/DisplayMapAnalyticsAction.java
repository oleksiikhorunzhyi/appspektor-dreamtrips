package com.worldventures.dreamtrips.wallet.analytics.locatecard.action;

import com.worldventures.dreamtrips.core.utils.tracksystem.AdobeTracker;
import com.worldventures.dreamtrips.core.utils.tracksystem.AnalyticsEvent;
import com.worldventures.dreamtrips.core.utils.tracksystem.Attribute;
import com.worldventures.dreamtrips.wallet.domain.entity.lostcard.WalletCoordinates;

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
