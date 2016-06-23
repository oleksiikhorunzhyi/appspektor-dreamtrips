package com.worldventures.dreamtrips.modules.dtl.analytics;

import com.worldventures.dreamtrips.core.utils.tracksystem.AdobeTracker;
import com.worldventures.dreamtrips.core.utils.tracksystem.Analytics;

@Analytics(action = "Local:Restaurant-Listings", trackers = AdobeTracker.TRACKER_KEY)
public class MerchantsListingViewEvent extends DtlAnalyticsAction {
}
