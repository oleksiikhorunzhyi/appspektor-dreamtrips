package com.worldventures.dreamtrips.modules.trips.service.analytics;

import com.worldventures.core.service.analytics.AdobeTracker;
import com.worldventures.janet.analytics.AnalyticsEvent;
import com.worldventures.core.service.analytics.Attribute;
import com.worldventures.core.service.analytics.BaseAnalyticsAction;

@AnalyticsEvent(action = "dreamtrips", trackers = AdobeTracker.TRACKER_KEY)
public class ViewDreamTripsAdobeAnalyticAction extends BaseAnalyticsAction {

   @Attribute("list") String list = "1";

}
