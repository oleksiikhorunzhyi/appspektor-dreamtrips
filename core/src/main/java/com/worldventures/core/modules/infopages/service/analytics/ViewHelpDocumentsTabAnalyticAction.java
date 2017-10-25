package com.worldventures.core.modules.infopages.service.analytics;

import com.worldventures.core.service.analytics.AnalyticsEvent;
import com.worldventures.core.service.analytics.BaseAnalyticsAction;
import com.worldventures.core.service.analytics.AdobeTracker;

@AnalyticsEvent(action = "Help:Documents",
                trackers = AdobeTracker.TRACKER_KEY)
public class ViewHelpDocumentsTabAnalyticAction extends BaseAnalyticsAction {
}
