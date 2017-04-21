package com.worldventures.dreamtrips.modules.infopages.service.analytics;

import com.worldventures.dreamtrips.core.utils.tracksystem.AdobeTracker;
import com.worldventures.dreamtrips.core.utils.tracksystem.AnalyticsEvent;
import com.worldventures.dreamtrips.core.utils.tracksystem.BaseAnalyticsAction;

@AnalyticsEvent(action = "Help:Documents",
                trackers = AdobeTracker.TRACKER_KEY)
public class ViewDocumentsTabAnalyticAction extends BaseAnalyticsAction {
}
