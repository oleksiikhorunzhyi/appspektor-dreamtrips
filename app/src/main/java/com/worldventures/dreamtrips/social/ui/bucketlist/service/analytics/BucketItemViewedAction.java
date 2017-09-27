package com.worldventures.dreamtrips.social.ui.bucketlist.service.analytics;

import com.worldventures.dreamtrips.core.utils.tracksystem.AnalyticsEvent;
import com.worldventures.dreamtrips.core.utils.tracksystem.ApptentiveTracker;
import com.worldventures.dreamtrips.core.utils.tracksystem.BaseAnalyticsAction;

@AnalyticsEvent(category = "bl_item_view", trackers = ApptentiveTracker.TRACKER_KEY)
public class BucketItemViewedAction extends BaseAnalyticsAction {
}
