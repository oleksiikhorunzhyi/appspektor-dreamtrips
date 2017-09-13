package com.worldventures.dreamtrips.modules.trips.service.analytics;

import android.text.TextUtils;

import com.worldventures.dreamtrips.core.utils.tracksystem.AdobeTracker;
import com.worldventures.dreamtrips.core.utils.tracksystem.AnalyticsEvent;
import com.worldventures.dreamtrips.core.utils.tracksystem.Attribute;
import com.worldventures.dreamtrips.core.utils.tracksystem.BaseAnalyticsAction;
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
