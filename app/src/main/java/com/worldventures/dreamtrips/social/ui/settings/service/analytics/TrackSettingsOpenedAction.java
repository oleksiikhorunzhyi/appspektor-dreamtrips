package com.worldventures.dreamtrips.social.ui.settings.service.analytics;

import com.worldventures.dreamtrips.core.utils.tracksystem.AdobeTracker;
import com.worldventures.dreamtrips.core.utils.tracksystem.AnalyticsEvent;
import com.worldventures.dreamtrips.core.utils.tracksystem.BaseAnalyticsAction;

@AnalyticsEvent(action = "Settings", trackers = AdobeTracker.TRACKER_KEY)
public class TrackSettingsOpenedAction extends BaseAnalyticsAction {
}
