package com.worldventures.dreamtrips.social.ui.feed.service.analytics;

import com.worldventures.dreamtrips.core.utils.tracksystem.AdobeTracker;
import com.worldventures.dreamtrips.core.utils.tracksystem.AnalyticsEvent;
import com.worldventures.dreamtrips.core.utils.tracksystem.Attribute;
import com.worldventures.dreamtrips.core.utils.tracksystem.BaseAnalyticsAction;


@AnalyticsEvent(action = "notifications", trackers = AdobeTracker.TRACKER_KEY)
public class LoadMoreNotificationsAnalyticAction extends BaseAnalyticsAction {

   @Attribute("load_more") String loadMore = "1";

}
