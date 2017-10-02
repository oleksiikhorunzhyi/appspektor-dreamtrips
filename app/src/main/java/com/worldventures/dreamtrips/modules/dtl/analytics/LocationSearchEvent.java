package com.worldventures.dreamtrips.modules.dtl.analytics;

import com.worldventures.core.service.analytics.AnalyticsEvent;
import com.worldventures.core.service.analytics.Attribute;
import com.worldventures.core.service.analytics.AdobeTracker;
import com.worldventures.dreamtrips.modules.dtl.model.location.DtlLocation;

@AnalyticsEvent(action = "local:City Search", trackers = AdobeTracker.TRACKER_KEY)
public class LocationSearchEvent extends DtlAnalyticsAction {

   @Attribute("dtlcitysearch") final String locationName;

   public static LocationSearchEvent create(DtlLocation location) {
      return new LocationSearchEvent(location);
   }

   private LocationSearchEvent(DtlLocation location) {
      this.locationName = location.analyticsName();
   }
}
