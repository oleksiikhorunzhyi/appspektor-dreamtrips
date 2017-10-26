package com.worldventures.dreamtrips.social.ui.tripsimages.service.analytics;

import com.worldventures.janet.analytics.AnalyticsEvent;
import com.worldventures.core.service.analytics.ApptentiveTracker;
import com.worldventures.core.service.analytics.BaseAnalyticsAction;

@AnalyticsEvent(action = "membership",
                category = "nav_menu",
                trackers = ApptentiveTracker.TRACKER_KEY)
public class TripImageVideo360StartedPlaying extends BaseAnalyticsAction {

}
