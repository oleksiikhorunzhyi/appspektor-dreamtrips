package com.worldventures.dreamtrips.social.ui.bucketlist.service.analytics;

import com.worldventures.dreamtrips.core.utils.tracksystem.AnalyticsEvent;
import com.worldventures.dreamtrips.core.utils.tracksystem.ApptentiveTracker;
import com.worldventures.dreamtrips.core.utils.tracksystem.BaseAnalyticsAction;

@AnalyticsEvent(action = "bucketlist",
                category = "nav_menu",
                trackers = ApptentiveTracker.TRACKER_KEY)
public class ApptentiveBucketListViewedAction extends BaseAnalyticsAction {
}
