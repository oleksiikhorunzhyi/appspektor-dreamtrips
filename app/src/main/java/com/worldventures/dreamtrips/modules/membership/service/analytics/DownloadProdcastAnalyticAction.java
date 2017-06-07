package com.worldventures.dreamtrips.modules.membership.service.analytics;

import com.worldventures.dreamtrips.core.utils.tracksystem.AdobeTracker;
import com.worldventures.dreamtrips.core.utils.tracksystem.AnalyticsEvent;
import com.worldventures.dreamtrips.core.utils.tracksystem.BaseAnalyticsAction;

@AnalyticsEvent(action = "membership:podcasts:download",
                trackers = AdobeTracker.TRACKER_KEY)
public class DownloadProdcastAnalyticAction extends BaseAnalyticsAction {



}
