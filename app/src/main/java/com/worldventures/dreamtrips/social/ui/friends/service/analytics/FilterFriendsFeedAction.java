package com.worldventures.dreamtrips.social.ui.friends.service.analytics;

import com.worldventures.dreamtrips.core.utils.tracksystem.AdobeTracker;
import com.worldventures.dreamtrips.core.utils.tracksystem.AnalyticsEvent;
import com.worldventures.dreamtrips.core.utils.tracksystem.AttributeMap;
import com.worldventures.dreamtrips.core.utils.tracksystem.BaseAnalyticsAction;

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
