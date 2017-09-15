package com.worldventures.dreamtrips.modules.feed.service.analytics;

import com.worldventures.dreamtrips.core.utils.tracksystem.AdobeTracker;
import com.worldventures.dreamtrips.core.utils.tracksystem.AnalyticsEvent;
import com.worldventures.dreamtrips.core.utils.tracksystem.Attribute;
import com.worldventures.dreamtrips.core.utils.tracksystem.AttributeMap;
import com.worldventures.dreamtrips.core.utils.tracksystem.BaseAnalyticsAction;
import com.worldventures.dreamtrips.social.ui.feed.model.FeedEntityHolder;

import java.util.HashMap;
import java.util.Map;

@AnalyticsEvent(action = "activity_feed", trackers = AdobeTracker.TRACKER_KEY)
public class LikeAction extends BaseAnalyticsAction {

   @Attribute("like")
   final String like = "1";

   @AttributeMap
   final Map<String, String> attributeMap = new HashMap<>();

   public LikeAction(FeedEntityHolder.Type type, String uid) {
      attributeMap.put(FeedAnalyticsUtils.getIdAttributeName(type), uid);
   }
}
