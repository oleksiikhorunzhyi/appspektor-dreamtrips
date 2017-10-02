package com.messenger.analytics;

import com.worldventures.core.service.analytics.AdobeTracker;
import com.worldventures.core.service.analytics.AnalyticsEvent;
import com.worldventures.core.service.analytics.BaseAnalyticsAction;

@AnalyticsEvent(action = "Messenger:Leave Group Chat", trackers = AdobeTracker.TRACKER_KEY)
public class LeaveConversationActon extends BaseAnalyticsAction {
}
