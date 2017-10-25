package com.worldventures.dreamtrips.social.ui.membership.service.analytics;

import com.worldventures.core.service.analytics.AdobeTracker;
import com.worldventures.core.service.analytics.AnalyticsEvent;
import com.worldventures.core.service.analytics.BaseAnalyticsAction;

@AnalyticsEvent(action = "membership:podcasts:download",
                trackers = AdobeTracker.TRACKER_KEY)
public class DownloadProdcastAnalyticAction extends BaseAnalyticsAction {


}
