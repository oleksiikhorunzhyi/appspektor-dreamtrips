package com.messenger.analytics;

import com.worldventures.core.service.analytics.AdobeTracker;
import com.worldventures.core.service.analytics.AnalyticsEvent;
import com.worldventures.core.service.analytics.BaseAnalyticsAction;

@AnalyticsEvent(action = "Messenger:Group Chat Settings", trackers = AdobeTracker.TRACKER_KEY)
public class GroupSettingsOpenedAction extends BaseAnalyticsAction {
}
