package com.worldventures.dreamtrips.wallet.analytics.locatecard;

import com.worldventures.dreamtrips.core.utils.tracksystem.AdobeTracker;
import com.worldventures.dreamtrips.core.utils.tracksystem.AnalyticsEvent;
import com.worldventures.dreamtrips.core.utils.tracksystem.Attribute;

@AnalyticsEvent(action = "dta:wallet:settings:locate smartcard:display location",
                trackers = AdobeTracker.TRACKER_KEY)
public class DisplayLocateSmartCardAction extends BaseLocateSmartCardAction {

   @Attribute("trackingenabled") String trackingEnabled = "Yes";
   @Attribute("locationavailable") String locationAvailable;

   private DisplayLocateSmartCardAction(String locationAvailable) {
      this.locationAvailable = locationAvailable;
   }

   public static DisplayLocateSmartCardAction forLocateSmartCard(boolean lastLocationAvailable) {
      String locationAvailable = lastLocationAvailable ? "Yes" : "No";
      return new DisplayLocateSmartCardAction(locationAvailable);
   }
}
