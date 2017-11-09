package com.worldventures.dreamtrips.modules.trips.service.analytics;

import com.worldventures.core.service.analytics.AdobeTracker;
import com.worldventures.janet.analytics.AnalyticsEvent;
import com.worldventures.core.service.analytics.Attribute;
import com.worldventures.core.service.analytics.BaseAnalyticsAction;
import com.worldventures.dreamtrips.modules.trips.model.TripsFilterDataAnalyticsWrapper;

@AnalyticsEvent(action = "dreamtrips", trackers = AdobeTracker.TRACKER_KEY)
public class AcceptDreamTripsFiltersAnalyticAction extends BaseAnalyticsAction {

   @Attribute("filter") final String filter = "1";
   @Attribute("tripfilters") String filters;
   @Attribute("tripregionfilters") String regionFilters;
   @Attribute("tripthemefilters") String themeFilters;

   public AcceptDreamTripsFiltersAnalyticAction(TripsFilterDataAnalyticsWrapper filterData) {
      this.filters = filterData.getFilterAnalyticString();
      this.regionFilters = filterData.getAcceptedRegionsAnalyticString();
      this.themeFilters = filterData.getAcceptedActivitiesAnalyticString();
   }
}
