package com.worldventures.core.modules.infopages.service.analytics;

import com.worldventures.core.service.analytics.AdobeTracker;
import com.worldventures.core.service.analytics.BaseAnalyticsAction;
import com.worldventures.janet.analytics.AnalyticsEvent;

@AnalyticsEvent(action = "Legal Documents",
                trackers = AdobeTracker.TRACKER_KEY)
public class ViewLegalDocumentsAnalyticAction extends BaseAnalyticsAction {
}
