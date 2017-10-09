package com.worldventures.dreamtrips.social.ui.feed.service.analytics;

import com.worldventures.core.service.analytics.AdobeTracker;
import com.worldventures.core.service.analytics.AnalyticsEvent;
import com.worldventures.core.service.analytics.AttributeMap;
import com.worldventures.core.service.analytics.BaseAnalyticsAction;
import com.worldventures.dreamtrips.modules.trips.model.TripModel;
import com.worldventures.dreamtrips.social.ui.bucketlist.model.BucketItem;
import com.worldventures.dreamtrips.social.ui.feed.model.FeedEntity;
import com.worldventures.dreamtrips.social.ui.feed.model.FeedEntityHolder;
import com.worldventures.dreamtrips.social.ui.feed.model.TextualPost;
import com.worldventures.dreamtrips.social.ui.tripsimages.model.Photo;

import java.util.HashMap;
import java.util.Map;

@AnalyticsEvent(action = "activity_feed", trackers = AdobeTracker.TRACKER_KEY)
public class CommentAction extends BaseAnalyticsAction {

   public static final String ATTRIBUTE_ADD_COMMENT = "comment";
   public static final String ATTRIBUTE_DELETE_COMMENT = "delete_comment";
   public static final String ATTRIBUTE_EDIT_COMMENT = "edit_comment";

   @AttributeMap final Map<String, String> attributeMap = new HashMap<>();

   public CommentAction(String actionAttributeName, FeedEntity feedEntity) {
      attributeMap.put(actionAttributeName, "1");
      attributeMap.put(FeedAnalyticsUtils.getIdAttributeName(mapType(feedEntity)), feedEntity.getUid());
   }

   public static CommentAction add(FeedEntity feedEntity) {
      return new CommentAction(ATTRIBUTE_ADD_COMMENT, feedEntity);
   }

   public static CommentAction edit(FeedEntity feedEntity) {
      return new CommentAction(ATTRIBUTE_EDIT_COMMENT, feedEntity);
   }

   public static CommentAction delete(FeedEntity feedEntity) {
      return new CommentAction(ATTRIBUTE_DELETE_COMMENT, feedEntity);
   }

   private FeedEntityHolder.Type mapType(FeedEntity feedEntity) {
      if (feedEntity instanceof BucketItem) {
         return FeedEntityHolder.Type.BUCKET_LIST_ITEM;
      }
      if (feedEntity instanceof Photo) {
         return FeedEntityHolder.Type.PHOTO;
      }
      if (feedEntity instanceof TextualPost) {
         return FeedEntityHolder.Type.POST;
      }
      if (feedEntity instanceof TripModel) {
         return FeedEntityHolder.Type.TRIP;
      }
      return FeedEntityHolder.Type.UNDEFINED;
   }
}
