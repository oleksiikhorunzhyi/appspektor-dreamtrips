package com.worldventures.dreamtrips.social.ui.bucketlist.service.analytics;

import com.worldventures.dreamtrips.core.utils.tracksystem.AdobeTracker;
import com.worldventures.dreamtrips.core.utils.tracksystem.AnalyticsEvent;
import com.worldventures.dreamtrips.core.utils.tracksystem.Attribute;
import com.worldventures.dreamtrips.core.utils.tracksystem.BaseAnalyticsAction;
import com.worldventures.dreamtrips.social.ui.bucketlist.model.BucketItem;

@AnalyticsEvent(action = "bucketlist",
                trackers = AdobeTracker.TRACKER_KEY)
public class AdobeBucketListViewedAction extends BaseAnalyticsAction {

   @Attribute("list")
   String list = "1";
}
