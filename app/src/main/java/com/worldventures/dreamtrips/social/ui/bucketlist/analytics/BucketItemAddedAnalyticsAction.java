package com.worldventures.dreamtrips.social.ui.bucketlist.analytics;

import com.worldventures.core.service.analytics.AdobeTracker;
import com.worldventures.core.service.analytics.AnalyticsEvent;
import com.worldventures.core.service.analytics.Attribute;
import com.worldventures.core.service.analytics.BaseAnalyticsAction;

@AnalyticsEvent(action = "bucketlist:add",
                trackers = AdobeTracker.TRACKER_KEY)
public class BucketItemAddedAnalyticsAction extends BaseAnalyticsAction {

   @Attribute("bucketlistadd") String bucketName;

   public BucketItemAddedAnalyticsAction(String bucketName) {
      this.bucketName = bucketName;
   }
}
