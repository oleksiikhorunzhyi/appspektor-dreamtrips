package com.worldventures.dreamtrips.modules.trips.service.analytics;

import com.worldventures.core.service.analytics.AnalyticsEvent;
import com.worldventures.core.service.analytics.ApptentiveTracker;
import com.worldventures.core.service.analytics.BaseAnalyticsAction;

@AnalyticsEvent(action = "dreamtrips", category = "nav_menu", trackers = ApptentiveTracker.TRACKER_KEY)
public class ViewDreamTripsApptentiveAnalyticAction extends BaseAnalyticsAction {
}

