package com.worldventures.dreamtrips.modules.infopages.service.analytics;

import com.worldventures.dreamtrips.core.utils.tracksystem.AdobeTracker;
import com.worldventures.dreamtrips.core.utils.tracksystem.AnalyticsEvent;
import com.worldventures.dreamtrips.core.utils.tracksystem.Attribute;
import com.worldventures.dreamtrips.core.utils.tracksystem.BaseAnalyticsAction;

@AnalyticsEvent(action = "book_travel",
                trackers = AdobeTracker.TRACKER_KEY)
public class ViewBookTravelAnalytics extends BaseAnalyticsAction {

   @Attribute("view") String view = "1";

   @Override
   public boolean equals(Object obj) {
      return obj instanceof ViewBookTravelAnalytics;
   }
}
