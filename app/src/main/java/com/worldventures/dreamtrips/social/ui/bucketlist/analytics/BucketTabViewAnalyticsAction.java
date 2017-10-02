package com.worldventures.dreamtrips.social.ui.bucketlist.analytics;

import com.worldventures.core.service.analytics.ActionPart;
import com.worldventures.core.service.analytics.AdobeTracker;
import com.worldventures.core.service.analytics.AnalyticsEvent;
import com.worldventures.core.service.analytics.BaseAnalyticsAction;
import com.worldventures.dreamtrips.social.ui.bucketlist.model.BucketItem;
import com.worldventures.dreamtrips.social.ui.bucketlist.service.analytics.BucketAnalyticsUtils;

@AnalyticsEvent(action = "bucketlist:${tabTitle}",
                trackers = AdobeTracker.TRACKER_KEY)
public class BucketTabViewAnalyticsAction extends BaseAnalyticsAction {

   @ActionPart String tabTitle;

   public BucketTabViewAnalyticsAction(BucketItem.BucketType type) {
      this.tabTitle = BucketAnalyticsUtils.getAnalyticsName(type);
   }
}
