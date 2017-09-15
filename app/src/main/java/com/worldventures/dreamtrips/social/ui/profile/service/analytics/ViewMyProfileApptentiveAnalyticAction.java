package com.worldventures.dreamtrips.social.ui.profile.service.analytics;

import com.worldventures.dreamtrips.core.utils.tracksystem.AnalyticsEvent;
import com.worldventures.dreamtrips.core.utils.tracksystem.ApptentiveTracker;
import com.worldventures.dreamtrips.core.utils.tracksystem.BaseAnalyticsAction;

@AnalyticsEvent(action = "profile", category = "nav_menu", trackers = ApptentiveTracker.TRACKER_KEY)
public class ViewMyProfileApptentiveAnalyticAction extends BaseAnalyticsAction {
}
