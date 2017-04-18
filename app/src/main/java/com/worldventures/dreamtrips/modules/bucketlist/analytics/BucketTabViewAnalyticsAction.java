package com.worldventures.dreamtrips.modules.bucketlist.analytics;

import com.worldventures.dreamtrips.core.utils.tracksystem.ActionPart;
import com.worldventures.dreamtrips.core.utils.tracksystem.AdobeTracker;
import com.worldventures.dreamtrips.core.utils.tracksystem.AnalyticsEvent;
import com.worldventures.dreamtrips.core.utils.tracksystem.BaseAnalyticsAction;
import com.worldventures.dreamtrips.modules.bucketlist.model.BucketItem;

@AnalyticsEvent(action = "bucketlist:${tabTitle}",
                trackers = AdobeTracker.TRACKER_KEY)
public class BucketTabViewAnalyticsAction extends BaseAnalyticsAction {

   @ActionPart String tabTitle;

   public BucketTabViewAnalyticsAction(BucketItem.BucketType type) {
      this.tabTitle = type.getAnalyticsName();
   }
}
