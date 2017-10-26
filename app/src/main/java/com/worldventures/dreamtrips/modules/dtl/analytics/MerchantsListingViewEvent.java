package com.worldventures.dreamtrips.modules.dtl.analytics;

import com.worldventures.janet.analytics.AnalyticsEvent;
import com.worldventures.core.service.analytics.AdobeTracker;

@AnalyticsEvent(action = "local:Restaurant-Listings", trackers = AdobeTracker.TRACKER_KEY)
public class MerchantsListingViewEvent extends DtlAnalyticsAction {
}
