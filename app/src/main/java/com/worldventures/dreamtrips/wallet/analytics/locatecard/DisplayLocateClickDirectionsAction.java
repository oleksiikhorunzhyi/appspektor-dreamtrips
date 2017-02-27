package com.worldventures.dreamtrips.wallet.analytics.locatecard;

import com.worldventures.dreamtrips.core.utils.tracksystem.AdobeTracker;
import com.worldventures.dreamtrips.core.utils.tracksystem.AnalyticsEvent;
import com.worldventures.dreamtrips.core.utils.tracksystem.Attribute;

@AnalyticsEvent(action = "dta:wallet:settings:locate smartcard:display location:get directions",
                trackers = AdobeTracker.TRACKER_KEY)
public class DisplayLocateClickDirectionsAction extends BaseLocateSmartCardAction {

   @Attribute("getdirections") String getDirections = "1";
   @Attribute("trackingenabled") String trackingEnabled = "Yes";
   @Attribute("locationavailable") String locationAvailable;

   private DisplayLocateClickDirectionsAction(String locationAvailable) {
      this.locationAvailable = locationAvailable;
   }

   public static DisplayLocateClickDirectionsAction forLocateSmartCard(boolean lastLocationAvailable) {
      String locationAvailable = lastLocationAvailable ? "Yes" : "No";
      return new DisplayLocateClickDirectionsAction(locationAvailable);
   }
}
