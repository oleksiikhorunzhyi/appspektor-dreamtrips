package com.worldventures.dreamtrips.wallet.analytics.locatecard;

import com.worldventures.dreamtrips.core.utils.tracksystem.AdobeTracker;
import com.worldventures.dreamtrips.core.utils.tracksystem.AnalyticsEvent;
import com.worldventures.dreamtrips.core.utils.tracksystem.Attribute;

@AnalyticsEvent(action = "dta:wallet:settings:locate smartcard",
                trackers = AdobeTracker.TRACKER_KEY)
public class OpenLocateSmartCardAction extends BaseLocateSmartCardAction {

   @Attribute("trackingenabled") String trackingEnabled;

   private OpenLocateSmartCardAction(String trackingEnabled) {
      this.trackingEnabled = trackingEnabled;
   }

   public static OpenLocateSmartCardAction forLocateSmartCard(boolean isTrackingEnable) {
      String trackingEnabled = isTrackingEnable ? "Yes" : "No";
      return new OpenLocateSmartCardAction(trackingEnabled);
   }
}
