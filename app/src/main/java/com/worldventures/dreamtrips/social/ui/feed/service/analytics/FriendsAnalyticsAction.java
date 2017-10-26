package com.worldventures.dreamtrips.social.ui.feed.service.analytics;

import com.worldventures.core.service.analytics.AdobeTracker;
import com.worldventures.janet.analytics.AnalyticsEvent;
import com.worldventures.core.service.analytics.AttributeMap;
import com.worldventures.core.service.analytics.BaseAnalyticsAction;

import java.util.HashMap;

@AnalyticsEvent(action = "friends_activity", trackers = AdobeTracker.TRACKER_KEY)
public class FriendsAnalyticsAction extends BaseAnalyticsAction {

   @AttributeMap
   HashMap<String, String> map = new HashMap<>();

   private FriendsAnalyticsAction(String attribute) {
      map.put(attribute, "1");
   }

   public static FriendsAnalyticsAction openFriends() {
      return new FriendsAnalyticsAction("open_friends");
   }

   public static FriendsAnalyticsAction addFriends() {
      return new FriendsAnalyticsAction("add_friends");
   }

   public static FriendsAnalyticsAction searchFriends() {
      return new FriendsAnalyticsAction("search_friends");
   }
}
