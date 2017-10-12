package com.worldventures.core.modules.infopages.service.analytics;

import com.worldventures.core.service.analytics.AnalyticsEvent;
import com.worldventures.core.service.analytics.Attribute;
import com.worldventures.core.service.analytics.BaseAnalyticsAction;
import com.worldventures.core.service.analytics.AdobeTracker;

@AnalyticsEvent(action = "book_travel",
                trackers = AdobeTracker.TRACKER_KEY)
public class ViewBookTravelAnalytics extends BaseAnalyticsAction {

   @Attribute("view") String view = "1";

   @Override
   public boolean equals(Object obj) {
      return obj instanceof ViewBookTravelAnalytics;
   }
}
