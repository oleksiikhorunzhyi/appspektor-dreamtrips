package com.worldventures.dreamtrips.modules.dtl.analytics;

import com.worldventures.dreamtrips.core.utils.tracksystem.AdobeTracker;
import com.worldventures.dreamtrips.core.utils.tracksystem.Analytics;
import com.worldventures.dreamtrips.core.utils.tracksystem.Attribute;

@Analytics(action = "local:Restaurant-Listings:Restaurant Search",
        trackers = AdobeTracker.TRACKER_KEY)
public class MerchantFromSearchEvent extends DtlAnalyticsAction {

    @Attribute("searchquery")
    final String query;

    public MerchantFromSearchEvent(String query) {
        this.query = query;
    }
}
