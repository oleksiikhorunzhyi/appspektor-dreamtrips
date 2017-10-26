package com.worldventures.dreamtrips.social.ui.friends.service.analytics;

import com.worldventures.core.service.analytics.AdobeTracker;
import com.worldventures.janet.analytics.AnalyticsEvent;
import com.worldventures.core.service.analytics.AttributeMap;
import com.worldventures.core.service.analytics.BaseAnalyticsAction;

import java.util.HashMap;
import java.util.Map;

@AnalyticsEvent(action = "friends_activity", trackers = AdobeTracker.TRACKER_KEY)
public class FilterFriendsFeedAction extends BaseAnalyticsAction {

   @AttributeMap
   Map<String, String> map = new HashMap<>();

   public FilterFriendsFeedAction(String circleType) {
      map.put("friends_filter_" + circleType, "1");
   }
}
