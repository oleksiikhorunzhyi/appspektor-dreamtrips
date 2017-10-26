package com.worldventures.dreamtrips.modules.trips.service.analytics;

import android.text.TextUtils;

import com.worldventures.core.service.analytics.AdobeTracker;
import com.worldventures.janet.analytics.AnalyticsEvent;
import com.worldventures.core.service.analytics.Attribute;
import com.worldventures.core.service.analytics.BaseAnalyticsAction;
import com.worldventures.dreamtrips.modules.trips.model.TripsFilterDataAnalyticsWrapper;

@AnalyticsEvent(action = "dreamtrips:tripdetail", trackers = AdobeTracker.TRACKER_KEY)
public class ViewTripDetailsAnalyticAction extends BaseAnalyticsAction {

   @Attribute("trip_id") String tripId;
   @Attribute("view") final String view = "1";
   @Attribute("tripsearch") String searchQuery;
   @Attribute("tripfilters") String filters;
   @Attribute("tripregionfilters") String regionFilters;
   @Attribute("tripthemefilters") String themeFilters;

   public ViewTripDetailsAnalyticAction(String tripId, String tripName, String searchQuery,
         TripsFilterDataAnalyticsWrapper filterData) {

      this.tripId = tripName + "-" + tripId;
      this.filters = filterData.getFilterAnalyticString();
      this.regionFilters = filterData.getAcceptedRegionsAnalyticString();
      this.themeFilters = filterData.getAcceptedActivitiesAnalyticString();
      if (!TextUtils.isEmpty(searchQuery)) this.searchQuery = searchQuery;
   }
}
