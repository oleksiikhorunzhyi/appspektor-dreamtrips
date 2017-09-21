package com.worldventures.dreamtrips.modules.trips.service.analytics;

import com.worldventures.dreamtrips.core.utils.tracksystem.AnalyticsEvent;
import com.worldventures.dreamtrips.core.utils.tracksystem.ApptentiveTracker;
import com.worldventures.dreamtrips.core.utils.tracksystem.BaseAnalyticsAction;

@AnalyticsEvent(category = "inspireme_share", trackers = ApptentiveTracker.TRACKER_KEY)
public class ShareInspirationImageAnalyticAction extends BaseAnalyticsAction {

}
