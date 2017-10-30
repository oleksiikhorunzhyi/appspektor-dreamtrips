package com.worldventures.dreamtrips.social.ui.membership.service.analytics;

import com.worldventures.janet.analytics.AnalyticsEvent;
import com.worldventures.core.service.analytics.ApptentiveTracker;
import com.worldventures.core.service.analytics.BaseAnalyticsAction;

@AnalyticsEvent(action = "rep_tools:invite_share",
                category = "nav_menu",
                trackers = ApptentiveTracker.TRACKER_KEY)
public class InviteShareTemplateAction extends BaseAnalyticsAction {
}
