package com.worldventures.dreamtrips.modules.trips.service.analytics;

import com.worldventures.dreamtrips.core.utils.tracksystem.AdobeTracker;
import com.worldventures.dreamtrips.core.utils.tracksystem.AnalyticsEvent;
import com.worldventures.dreamtrips.core.utils.tracksystem.Attribute;
import com.worldventures.dreamtrips.core.utils.tracksystem.BaseAnalyticsAction;
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
