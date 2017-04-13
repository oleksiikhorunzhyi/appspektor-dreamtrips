package com.worldventures.dreamtrips.modules.bucketlist.analytics;

import com.worldventures.dreamtrips.core.utils.tracksystem.AdobeTracker;
import com.worldventures.dreamtrips.core.utils.tracksystem.AnalyticsEvent;
import com.worldventures.dreamtrips.core.utils.tracksystem.Attribute;
import com.worldventures.dreamtrips.core.utils.tracksystem.BaseAnalyticsAction;

@AnalyticsEvent(action = "bucketlist:add",
                trackers = AdobeTracker.TRACKER_KEY)
public class BucketItemAddedAnalyticsAction extends BaseAnalyticsAction {

   @Attribute("bucketlistadd") String bucketName;

   public BucketItemAddedAnalyticsAction(String bucketName) {
      this.bucketName = bucketName;
   }
}
