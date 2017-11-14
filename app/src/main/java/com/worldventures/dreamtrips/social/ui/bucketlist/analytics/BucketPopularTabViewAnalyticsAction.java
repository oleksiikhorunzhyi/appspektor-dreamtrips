package com.worldventures.dreamtrips.social.ui.bucketlist.analytics;

import com.worldventures.core.service.analytics.ActionPart;
import com.worldventures.core.service.analytics.AdobeTracker;
import com.worldventures.janet.analytics.AnalyticsEvent;
import com.worldventures.core.service.analytics.BaseAnalyticsAction;
import com.worldventures.dreamtrips.social.ui.bucketlist.model.BucketItem;
import com.worldventures.dreamtrips.social.ui.bucketlist.service.analytics.BucketAnalyticsUtils;

@AnalyticsEvent(action = "bucketlist:popular bucket lists:${tabTitle}",
                trackers = AdobeTracker.TRACKER_KEY)
public class BucketPopularTabViewAnalyticsAction extends BaseAnalyticsAction {

   @ActionPart String tabTitle;

   public BucketPopularTabViewAnalyticsAction(BucketItem.BucketType bucketType) {
      this.tabTitle = BucketAnalyticsUtils.getAnalyticsName(bucketType);
   }
}
