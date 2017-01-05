package com.worldventures.dreamtrips.modules.dtl.analytics;

import android.location.Location;

import com.worldventures.dreamtrips.core.utils.tracksystem.AdobeTracker;
import com.worldventures.dreamtrips.core.utils.tracksystem.AnalyticsEvent;
import com.worldventures.dreamtrips.core.utils.tracksystem.Attribute;
import com.worldventures.dreamtrips.modules.dtl.helper.inflater.MerchantAttributes;

import java.util.Locale;

@AnalyticsEvent(action = "local:Restaurant-Listings:Merchant View:Check-In",
                trackers = AdobeTracker.TRACKER_KEY)
public class CheckinEvent extends MerchantAnalyticsAction {

   @Attribute("checkin") final String attribute = "1";

   @Attribute("areperksavail") final String perksAvailable;

   @Attribute("arepointsavail") final String pointsAvailable;

   @Attribute("coordinates") final String coordinates;

   public CheckinEvent(MerchantAttributes merchantAttributes, Location location) {
      super(merchantAttributes);
      perksAvailable = merchantAttributes.hasPerks() ? "Yes" : "No";
      pointsAvailable = merchantAttributes.hasPoints() ? "Yes" : "No";
      coordinates = String.format(Locale.US, "%f,%f", location.getLatitude(), location.getLongitude());
   }
}
