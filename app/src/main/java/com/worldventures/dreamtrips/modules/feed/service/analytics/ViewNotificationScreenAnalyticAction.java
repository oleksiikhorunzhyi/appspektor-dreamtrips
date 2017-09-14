package com.worldventures.dreamtrips.modules.feed.service.analytics;

import com.worldventures.dreamtrips.core.utils.tracksystem.AdobeTracker;
import com.worldventures.dreamtrips.core.utils.tracksystem.AnalyticsEvent;
import com.worldventures.dreamtrips.core.utils.tracksystem.Attribute;
import com.worldventures.dreamtrips.core.utils.tracksystem.BaseAnalyticsAction;

@AnalyticsEvent(action = "notifications", trackers = AdobeTracker.TRACKER_KEY)
public class ViewNotificationScreenAnalyticAction extends BaseAnalyticsAction {

   @Attribute("list") String list = "1";

}
