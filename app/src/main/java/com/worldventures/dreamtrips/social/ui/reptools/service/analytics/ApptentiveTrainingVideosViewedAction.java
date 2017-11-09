package com.worldventures.dreamtrips.social.ui.reptools.service.analytics;

import com.worldventures.janet.analytics.AnalyticsEvent;
import com.worldventures.core.service.analytics.ApptentiveTracker;
import com.worldventures.core.service.analytics.BaseAnalyticsAction;

@AnalyticsEvent(action = "training_videos",
                category = "nav_menu",
                trackers = ApptentiveTracker.TRACKER_KEY)
public class ApptentiveTrainingVideosViewedAction extends BaseAnalyticsAction {
}