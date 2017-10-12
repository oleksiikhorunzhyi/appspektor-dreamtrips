package com.worldventures.dreamtrips.social.ui.reptools.service.analytics;

import com.worldventures.dreamtrips.core.utils.tracksystem.AnalyticsEvent;
import com.worldventures.dreamtrips.core.utils.tracksystem.ApptentiveTracker;
import com.worldventures.dreamtrips.core.utils.tracksystem.BaseAnalyticsAction;

@AnalyticsEvent(action = "rep_enroll",
                category = "nav_menu",
                trackers = ApptentiveTracker.TRACKER_KEY)
public class ApptentiveEnrolRepViewedAction extends BaseAnalyticsAction {
}

