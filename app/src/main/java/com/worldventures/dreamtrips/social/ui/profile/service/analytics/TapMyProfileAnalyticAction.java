package com.worldventures.dreamtrips.social.ui.profile.service.analytics;

import com.worldventures.core.service.analytics.AdobeTracker;
import com.worldventures.core.service.analytics.AnalyticsEvent;
import com.worldventures.core.service.analytics.Attribute;
import com.worldventures.core.service.analytics.BaseAnalyticsAction;

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
