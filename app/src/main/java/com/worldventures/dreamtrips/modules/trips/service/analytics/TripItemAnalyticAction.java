package com.worldventures.dreamtrips.modules.trips.service.analytics;

import com.worldventures.core.service.analytics.AdobeTracker;
import com.worldventures.core.service.analytics.AnalyticsEvent;
import com.worldventures.core.service.analytics.Attribute;
import com.worldventures.core.service.analytics.BaseAnalyticsAction;

@AnalyticsEvent(action = "dreamtrips", trackers = AdobeTracker.TRACKER_KEY)
public final class TripItemAnalyticAction extends BaseAnalyticsAction {

   @Attribute("trip_id") String tripId;
   @Attribute("like") String like;
   @Attribute("add_to_bucket_list") String addToBucketList;

   private TripItemAnalyticAction() {
   }

   public static TripItemAnalyticAction likeAction(String tripId, String tripName) {
      TripItemAnalyticAction action = new TripItemAnalyticAction();
      action.tripId = getTripId(tripId, tripName);
      action.like = action.tripId;
      return action;
   }

   public static TripItemAnalyticAction addToBucketListAction(String tripId, String tripName) {
      TripItemAnalyticAction action = new TripItemAnalyticAction();
      action.tripId = getTripId(tripId, tripName);
      action.addToBucketList = action.tripId;
      return action;
   }

   private static String getTripId(String tripId, String tripName) {
      return tripName + "-" + tripId;
   }

}
