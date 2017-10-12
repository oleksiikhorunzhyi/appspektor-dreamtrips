package com.worldventures.dreamtrips.social.ui.feed.service.analytics;

import com.worldventures.core.service.analytics.AdobeTracker;
import com.worldventures.core.service.analytics.AnalyticsEvent;
import com.worldventures.core.service.analytics.Attribute;
import com.worldventures.core.service.analytics.BaseAnalyticsAction;

@AnalyticsEvent(action = "notifications", trackers = AdobeTracker.TRACKER_KEY)
public class LoadMoreNotificationsAnalyticAction extends BaseAnalyticsAction {

   @Attribute("load_more") String loadMore = "1";

}
