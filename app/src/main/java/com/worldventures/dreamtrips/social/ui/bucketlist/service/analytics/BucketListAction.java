package com.worldventures.dreamtrips.social.ui.bucketlist.service.analytics;

import com.worldventures.core.service.analytics.AdobeTracker;
import com.worldventures.core.service.analytics.AnalyticsEvent;
import com.worldventures.core.service.analytics.AttributeMap;
import com.worldventures.core.service.analytics.BaseAnalyticsAction;
import com.worldventures.dreamtrips.social.ui.bucketlist.model.BucketItem;

import java.util.HashMap;
import java.util.Map;

@AnalyticsEvent(action = "bucketlist", trackers = AdobeTracker.TRACKER_KEY)
public class BucketListAction extends BaseAnalyticsAction {

   private static final String ATTRIBUTE_FILTER = "filter";
   private static final String ATTRIBUTE_ADD_FROM_POPULAR = "add_from_popular";

   @AttributeMap
   Map<String, String> attributeMap = new HashMap<>();

   protected BucketListAction() {
   }

   public static BucketListAction filter(BucketItem.BucketType bucketType) {
      BucketListAction bucketListAction = new BucketListAction();
      bucketListAction.attributeMap.put(ATTRIBUTE_FILTER, BucketAnalyticsUtils.getAnalyticsName(bucketType));
      return bucketListAction;
   }

   public static BucketListAction addFromPopular(BucketItem.BucketType bucketType) {
      BucketListAction bucketListAction = new BucketListAction();
      bucketListAction.attributeMap.put(ATTRIBUTE_ADD_FROM_POPULAR, BucketAnalyticsUtils.getAnalyticsName(bucketType));
      return bucketListAction;
   }

}
