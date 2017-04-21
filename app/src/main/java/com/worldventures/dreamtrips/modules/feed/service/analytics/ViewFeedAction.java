package com.worldventures.dreamtrips.modules.feed.service.analytics;


import com.worldventures.dreamtrips.core.utils.tracksystem.AdobeTracker;
import com.worldventures.dreamtrips.core.utils.tracksystem.AnalyticsEvent;
import com.worldventures.dreamtrips.core.utils.tracksystem.BaseAnalyticsAction;

@AnalyticsEvent(action = "activity_feed",
                trackers = AdobeTracker.TRACKER_KEY)
public class ViewFeedAction extends BaseAnalyticsAction {
}
