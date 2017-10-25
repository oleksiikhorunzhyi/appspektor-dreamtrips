package com.worldventures.dreamtrips.social.ui.reptools.service.analytics;

import com.worldventures.core.service.analytics.AnalyticsEvent;
import com.worldventures.core.service.analytics.ApptentiveTracker;
import com.worldventures.core.service.analytics.BaseAnalyticsAction;

@AnalyticsEvent(action = "rep_enroll",
                category = "nav_menu",
                trackers = ApptentiveTracker.TRACKER_KEY)
public class ApptentiveEnrolRepViewedAction extends BaseAnalyticsAction {
}

