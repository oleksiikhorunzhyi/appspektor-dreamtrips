package com.worldventures.dreamtrips.social.ui.settings.service.analytics;

import com.worldventures.core.service.analytics.AdobeTracker;
import com.worldventures.core.service.analytics.AnalyticsEvent;
import com.worldventures.core.service.analytics.BaseAnalyticsAction;

@AnalyticsEvent(action = "Settings:Notifications", trackers = AdobeTracker.TRACKER_KEY)
public class TrackNotificationSettingsOpened extends BaseAnalyticsAction {
}
