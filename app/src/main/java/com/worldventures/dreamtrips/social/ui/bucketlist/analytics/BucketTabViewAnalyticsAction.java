package com.worldventures.dreamtrips.social.ui.bucketlist.analytics;

import com.worldventures.dreamtrips.core.utils.tracksystem.ActionPart;
import com.worldventures.dreamtrips.core.utils.tracksystem.AdobeTracker;
import com.worldventures.dreamtrips.core.utils.tracksystem.AnalyticsEvent;
import com.worldventures.dreamtrips.core.utils.tracksystem.BaseAnalyticsAction;
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
