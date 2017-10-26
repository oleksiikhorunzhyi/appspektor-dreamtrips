package com.worldventures.dreamtrips.social.ui.bucketlist.service.analytics;

import com.worldventures.core.service.analytics.AdobeTracker;
import com.worldventures.janet.analytics.AnalyticsEvent;
import com.worldventures.core.service.analytics.AttributeMap;
import com.worldventures.core.service.analytics.BaseAnalyticsAction;

import java.util.HashMap;
import java.util.Map;

@AnalyticsEvent(action = "bucketlist", trackers = AdobeTracker.TRACKER_KEY)
public class BucketItemAction extends BaseAnalyticsAction {

   public static final String ATTRIBUTE_BUCKET_ID = "bucket_list_id";

   public static final String ATTRIBUTE_VIEW = "view";
   public static final String ATTRIBUTE_SHARE = "share";
   public static final String ATTRIBUTE_MARK_AS_DONE = "mark_as_done";
   public static final String ATTRIBUTE_COMPLETE = "complete";

   @AttributeMap
   Map<String, String> attributeMap = new HashMap<>();

   protected BucketItemAction(String actionAttribute, String bucketUid) {
      attributeMap.put(actionAttribute, "1");
      attributeMap.put(ATTRIBUTE_BUCKET_ID, bucketUid);
   }

   public static BucketItemAction markAsDone(String bucketUid) {
      return new BucketItemAction(ATTRIBUTE_MARK_AS_DONE, bucketUid);
   }

   public static BucketItemAction view(String bucketUid) {
      return new BucketItemAction(ATTRIBUTE_VIEW, bucketUid);
   }

   public static BucketItemAction complete(String bucketUid) {
      return new BucketItemAction(ATTRIBUTE_COMPLETE, bucketUid);
   }

   public static BucketItemAction share(String bucketUid) {
      return new BucketItemAction(ATTRIBUTE_SHARE, bucketUid);
   }
}
