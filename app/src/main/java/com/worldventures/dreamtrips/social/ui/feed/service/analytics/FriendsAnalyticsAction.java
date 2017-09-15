package com.worldventures.dreamtrips.social.ui.feed.service.analytics;

import com.worldventures.dreamtrips.core.utils.tracksystem.AdobeTracker;
import com.worldventures.dreamtrips.core.utils.tracksystem.AnalyticsEvent;
import com.worldventures.dreamtrips.core.utils.tracksystem.AttributeMap;
import com.worldventures.dreamtrips.core.utils.tracksystem.BaseAnalyticsAction;

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
