package com.worldventures.dreamtrips.social.ui.bucketlist.service.analytics;

import com.worldventures.janet.analytics.AnalyticsEvent;
import com.worldventures.core.service.analytics.ApptentiveTracker;
import com.worldventures.core.service.analytics.BaseAnalyticsAction;

@AnalyticsEvent(action = "bucketlist",
                category = "nav_menu",
                trackers = ApptentiveTracker.TRACKER_KEY)
public class ApptentiveBucketListViewedAction extends BaseAnalyticsAction {
}
