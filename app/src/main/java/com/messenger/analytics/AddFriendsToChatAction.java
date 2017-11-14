package com.messenger.analytics;

import com.worldventures.core.service.analytics.AdobeTracker;
import com.worldventures.janet.analytics.AnalyticsEvent;
import com.worldventures.core.service.analytics.BaseAnalyticsAction;

@AnalyticsEvent(action = "Messenger:Add Friends to Chat", trackers = AdobeTracker.TRACKER_KEY)
public class AddFriendsToChatAction extends BaseAnalyticsAction {
}
