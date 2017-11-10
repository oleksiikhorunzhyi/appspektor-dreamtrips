package com.worldventures.dreamtrips.modules.trips.service.analytics;

import com.worldventures.core.service.analytics.AnalyticsEvent;
import com.worldventures.core.service.analytics.ApptentiveTracker;
import com.worldventures.core.service.analytics.BaseAnalyticsAction;

@AnalyticsEvent(category = "inspireme_share", trackers = ApptentiveTracker.TRACKER_KEY)
public class ShareInspirationImageAnalyticAction extends BaseAnalyticsAction {

}