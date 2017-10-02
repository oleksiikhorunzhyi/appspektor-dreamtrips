package com.worldventures.dreamtrips.social.ui.bucketlist.service.analytics;

import com.worldventures.core.service.analytics.AdobeTracker;
import com.worldventures.core.service.analytics.AnalyticsEvent;
import com.worldventures.core.service.analytics.Attribute;
import com.worldventures.core.service.analytics.BaseAnalyticsAction;

@AnalyticsEvent(action = "bucketlist",
                trackers = AdobeTracker.TRACKER_KEY)
public class AdobeBucketListViewedAction extends BaseAnalyticsAction {

   @Attribute("list")
   String list = "1";
}
