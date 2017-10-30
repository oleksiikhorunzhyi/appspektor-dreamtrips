package com.worldventures.dreamtrips.modules.dtl.analytics;

<<<<<<< HEAD
import com.worldventures.janet.analytics.AnalyticsEvent;
=======
import com.worldventures.core.service.analytics.AdobeTracker;
import com.worldventures.core.service.analytics.AnalyticsEvent;
>>>>>>> core/kotlin-code-analysis
import com.worldventures.core.service.analytics.Attribute;

@AnalyticsEvent(action = "local:Restaurant-Listings:More Listings", trackers = AdobeTracker.TRACKER_KEY)
public class MerchantsListingLoadmoreEvent extends MerchantsListingViewEvent {

   @Attribute("morelocalistings") final String attribute = "1";
}
