package com.worldventures.dreamtrips.modules.dtl.analytics;

import com.worldventures.dreamtrips.core.utils.tracksystem.AdobeTracker;
import com.worldventures.dreamtrips.core.utils.tracksystem.AnalyticsEvent;
import com.worldventures.dreamtrips.core.utils.tracksystem.Attribute;

@AnalyticsEvent(action = "local:Restaurant-Listings:Restaurant Search",
        trackers = AdobeTracker.TRACKER_KEY)
public class MerchantFromSearchEvent extends DtlAnalyticsAction {

    @Attribute("searchquery")
    final String query;

    public MerchantFromSearchEvent(String query) {
        this.query = query;
    }
}
