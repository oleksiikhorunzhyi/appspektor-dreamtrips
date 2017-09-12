package com.worldventures.dreamtrips.modules.profile.service.analytics;

import com.worldventures.dreamtrips.core.utils.tracksystem.AdobeTracker;
import com.worldventures.dreamtrips.core.utils.tracksystem.AnalyticsEvent;
import com.worldventures.dreamtrips.core.utils.tracksystem.Attribute;
import com.worldventures.dreamtrips.core.utils.tracksystem.BaseAnalyticsAction;

@AnalyticsEvent(action = "profile", trackers = AdobeTracker.TRACKER_KEY)
public class TapMyProfileAnalyticAction extends BaseAnalyticsAction {

   @Attribute("show_bucketlist") String bucketListAttribute;
   @Attribute("show_trips") String tripsAttribute;
   @Attribute("show_friends") String friendsAttribute;
   @Attribute("new_post") String postAttribute;

   public static TapMyProfileAnalyticAction tapOnBucketList() {
      TapMyProfileAnalyticAction action = new TapMyProfileAnalyticAction();
      action.bucketListAttribute = "1";
      return action;
   }

   public static TapMyProfileAnalyticAction tapOnTrips() {
      TapMyProfileAnalyticAction action = new TapMyProfileAnalyticAction();
      action.tripsAttribute = "1";
      return action;
   }

   public static TapMyProfileAnalyticAction tapOnFriends() {
      TapMyProfileAnalyticAction action = new TapMyProfileAnalyticAction();
      action.friendsAttribute = "1";
      return action;
   }

   public static TapMyProfileAnalyticAction tapOnPost() {
      TapMyProfileAnalyticAction action = new TapMyProfileAnalyticAction();
      action.postAttribute = "1";
      return action;
   }
}
