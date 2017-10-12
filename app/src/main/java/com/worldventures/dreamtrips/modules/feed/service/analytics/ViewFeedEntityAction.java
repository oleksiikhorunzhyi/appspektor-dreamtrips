package com.worldventures.dreamtrips.modules.feed.service.analytics;

import com.worldventures.dreamtrips.core.utils.tracksystem.ActionPart;
import com.worldventures.dreamtrips.core.utils.tracksystem.AdobeTracker;
import com.worldventures.dreamtrips.core.utils.tracksystem.AnalyticsEvent;
import com.worldventures.dreamtrips.core.utils.tracksystem.AttributeMap;
import com.worldventures.dreamtrips.core.utils.tracksystem.BaseAnalyticsAction;
import com.worldventures.dreamtrips.social.ui.feed.model.FeedEntityHolder;

import java.util.HashMap;
import java.util.Map;

@AnalyticsEvent(action = "${action}", trackers = AdobeTracker.TRACKER_KEY)
public class ViewFeedEntityAction extends BaseAnalyticsAction {

   @ActionPart final String action;

   @AttributeMap
   final Map<String, String> attributeMap = new HashMap<>();

   public ViewFeedEntityAction(String action, String actionAttribute, FeedEntityHolder.Type type, String uid) {
      this.action = action;
      attributeMap.put(actionAttribute, "1");
      attributeMap.put(FeedAnalyticsUtils.getIdAttributeName(type), uid);
   }

   public static ViewFeedEntityAction view(FeedEntityHolder.Type type, String uid) {
      return new ViewFeedEntityAction("dreamtrips:socialdetail", "view", type, uid);
   }

   public static ViewFeedEntityAction edit(FeedEntityHolder.Type type, String uid) {
      return new ViewFeedEntityAction("activity_feed", "edit", type, uid);
   }

   public static ViewFeedEntityAction delete(FeedEntityHolder.Type type, String uid) {
      return new ViewFeedEntityAction("activity_feed", "delete", type, uid);
   }
}
