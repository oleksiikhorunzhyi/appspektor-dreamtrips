package com.worldventures.dreamtrips.modules.dtl.analytics;

import com.worldventures.dreamtrips.core.utils.tracksystem.AdobeTracker;
import com.worldventures.dreamtrips.core.utils.tracksystem.AnalyticsEvent;
import com.worldventures.dreamtrips.core.utils.tracksystem.Attribute;
import com.worldventures.dreamtrips.modules.dtl.model.location.DtlExternalLocation;

@AnalyticsEvent(action = "local:City Search", trackers = AdobeTracker.TRACKER_KEY)
public class LocationSearchEvent extends DtlAnalyticsAction {

   @Attribute("dtlcitysearch") final String locationName;

   public static LocationSearchEvent create(DtlExternalLocation location) {
      return new LocationSearchEvent(location);
   }

   private LocationSearchEvent(DtlExternalLocation location) {
      this.locationName = location.getAnalyticsName();
   }
}
