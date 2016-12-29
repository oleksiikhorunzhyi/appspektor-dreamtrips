package com.worldventures.dreamtrips.modules.dtl.analytics;

import com.worldventures.dreamtrips.core.utils.tracksystem.AdobeTracker;
import com.worldventures.dreamtrips.core.utils.tracksystem.AnalyticsEvent;
import com.worldventures.dreamtrips.core.utils.tracksystem.Attribute;

@AnalyticsEvent(action = "local:Restaurant-Listings:More Listings", trackers = AdobeTracker.TRACKER_KEY)
public class MerchantsListingLoadmoreEvent extends MerchantsListingViewEvent {

   @Attribute("morelocalistings") final String attribute = "1";
}
