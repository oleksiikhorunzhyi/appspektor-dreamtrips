package com.worldventures.dreamtrips.social.ui.settings.service.analytics;

import com.worldventures.core.service.analytics.AdobeTracker;
import com.worldventures.janet.analytics.AnalyticsEvent;
import com.worldventures.core.service.analytics.BaseAnalyticsAction;

@AnalyticsEvent(action = "Settings", trackers = AdobeTracker.TRACKER_KEY)
public class TrackSettingsOpenedAction extends BaseAnalyticsAction {
}
