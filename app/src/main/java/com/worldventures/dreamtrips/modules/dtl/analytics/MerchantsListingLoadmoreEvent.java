package com.worldventures.dreamtrips.modules.dtl.analytics;

import com.worldventures.core.service.analytics.AdobeTracker;
import com.worldventures.core.service.analytics.Attribute;
import com.worldventures.janet.analytics.AnalyticsEvent;

@AnalyticsEvent(action = "local:Restaurant-Listings:More Listings", trackers = AdobeTracker.TRACKER_KEY)
public class MerchantsListingLoadmoreEvent extends MerchantsListingViewEvent {

   @Attribute("morelocalistings") final String attribute = "1";
}
