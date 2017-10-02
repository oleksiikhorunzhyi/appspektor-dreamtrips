package com.worldventures.dreamtrips.social.ui.infopages.service.analytics;

import com.worldventures.core.service.analytics.AdobeTracker;
import com.worldventures.core.service.analytics.AnalyticsEvent;
import com.worldventures.core.service.analytics.BaseAnalyticsAction;

@AnalyticsEvent(action = "Legal Documents",
                trackers = AdobeTracker.TRACKER_KEY)
public class ViewLegalDocumentsAnalyticAction extends BaseAnalyticsAction {
}
