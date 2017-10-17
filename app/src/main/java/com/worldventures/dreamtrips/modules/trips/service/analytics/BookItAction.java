package com.worldventures.dreamtrips.modules.trips.service.analytics;

import com.worldventures.core.service.analytics.AdobeTracker;
import com.worldventures.core.service.analytics.AnalyticsEvent;
import com.worldventures.core.service.analytics.ApptentiveTracker;
import com.worldventures.core.service.analytics.Attribute;
import com.worldventures.core.service.analytics.BaseAnalyticsAction;

@AnalyticsEvent(action = "dreamtrips:tripdetail",
                trackers = {AdobeTracker.TRACKER_KEY, ApptentiveTracker.TRACKER_KEY})
public class BookItAction extends BaseAnalyticsAction {

   @Attribute("book_it") final String bookIt = "1";
   @Attribute("trip_id") final String trip;

   public BookItAction(String tripId, String tripName) {
      this.trip = tripName + "-" + tripId;
   }
}
