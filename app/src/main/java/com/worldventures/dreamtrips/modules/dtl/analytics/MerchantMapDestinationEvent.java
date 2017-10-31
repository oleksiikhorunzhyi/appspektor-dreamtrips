package com.worldventures.dreamtrips.modules.dtl.analytics;

import android.location.Location;
import android.support.annotation.Nullable;

import com.worldventures.core.service.analytics.AdobeTracker;
import com.worldventures.core.service.analytics.Attribute;
import com.worldventures.dreamtrips.modules.dtl.model.merchant.Merchant;
import com.worldventures.janet.analytics.AnalyticsEvent;

@AnalyticsEvent(action = "local:Restaurant-Listings:directions", trackers = AdobeTracker.TRACKER_KEY)
public class MerchantMapDestinationEvent extends DtlAnalyticsAction {

   @Attribute("map") final String attribute = "1";

   @Attribute("coordinates_id") final String coordinates;

   public MerchantMapDestinationEvent(@Nullable Location location, Merchant merchant) {
      // location value format: "o=lat,lng;d=lat,lng", 'o' for user origin, 'd' for destination
      // can be "d=lat,lng" if user origin unknown when GPS disabled (user origin == null)
      StringBuilder stringBuilder = new StringBuilder();
      if (location != null) {
         stringBuilder.append("o=")
               .append(location.getLatitude())
               .append(",")
               .append(location.getLongitude())
               .append(";");
      }
      stringBuilder.append("d=")
            .append(merchant.coordinates().lat())
            .append(",")
            .append(merchant.coordinates().lng());
      coordinates = stringBuilder.toString();
   }
}
