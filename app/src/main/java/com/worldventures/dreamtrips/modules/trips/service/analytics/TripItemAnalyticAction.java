package com.worldventures.dreamtrips.modules.trips.service.analytics;

import com.worldventures.dreamtrips.core.utils.tracksystem.AdobeTracker;
import com.worldventures.dreamtrips.core.utils.tracksystem.AnalyticsEvent;
import com.worldventures.dreamtrips.core.utils.tracksystem.Attribute;
import com.worldventures.dreamtrips.core.utils.tracksystem.BaseAnalyticsAction;


@AnalyticsEvent(action = "dreamtrips", trackers = AdobeTracker.TRACKER_KEY)
public class TripItemAnalyticAction extends BaseAnalyticsAction {

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

   private static String getTripId (String tripId, String tripName) {
      return tripName + "-" + tripId;
   }

}
