package com.messenger.analytics;

import com.worldventures.dreamtrips.core.utils.tracksystem.AdobeTracker;
import com.worldventures.dreamtrips.core.utils.tracksystem.AnalyticsEvent;
import com.worldventures.dreamtrips.core.utils.tracksystem.BaseAnalyticsAction;

@AnalyticsEvent(action = "Messenger:Group Chat Settings", trackers = AdobeTracker.TRACKER_KEY)
public class GroupSettingsOpenedAction extends BaseAnalyticsAction {
}
