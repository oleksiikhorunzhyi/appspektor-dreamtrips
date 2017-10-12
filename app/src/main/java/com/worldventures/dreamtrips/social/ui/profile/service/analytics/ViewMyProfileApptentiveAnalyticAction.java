package com.worldventures.dreamtrips.social.ui.profile.service.analytics;

import com.worldventures.core.service.analytics.AnalyticsEvent;
import com.worldventures.core.service.analytics.ApptentiveTracker;
import com.worldventures.core.service.analytics.BaseAnalyticsAction;

@AnalyticsEvent(action = "profile", category = "nav_menu", trackers = ApptentiveTracker.TRACKER_KEY)
public class ViewMyProfileApptentiveAnalyticAction extends BaseAnalyticsAction {
}
