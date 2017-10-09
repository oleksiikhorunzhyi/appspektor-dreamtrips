package com.worldventures.dreamtrips.social.ui.feed.service.analytics;

import com.worldventures.core.service.analytics.AdobeTracker;
import com.worldventures.core.service.analytics.AnalyticsEvent;
import com.worldventures.core.service.analytics.Attribute;
import com.worldventures.core.service.analytics.AttributeMap;
import com.worldventures.core.service.analytics.BaseAnalyticsAction;
import com.worldventures.dreamtrips.social.ui.feed.model.FeedEntityHolder;

import java.util.HashMap;
import java.util.Map;

@AnalyticsEvent(action = "activity_feed", trackers = AdobeTracker.TRACKER_KEY)
public class LikeAction extends BaseAnalyticsAction {

   @Attribute("like") final String like = "1";

   @AttributeMap final Map<String, String> attributeMap = new HashMap<>();

   public LikeAction(FeedEntityHolder.Type type, String uid) {
      attributeMap.put(FeedAnalyticsUtils.getIdAttributeName(type), uid);
   }
}
