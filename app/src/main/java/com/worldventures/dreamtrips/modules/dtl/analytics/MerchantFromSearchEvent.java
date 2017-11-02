package com.worldventures.dreamtrips.modules.dtl.analytics;

import com.worldventures.core.service.analytics.AdobeTracker;
import com.worldventures.core.service.analytics.Attribute;
import com.worldventures.janet.analytics.AnalyticsEvent;

@AnalyticsEvent(action = "local:Restaurant-Listings:Restaurant Search",
                trackers = AdobeTracker.TRACKER_KEY)
public class MerchantFromSearchEvent extends DtlAnalyticsAction {

   @Attribute("searchquery") final String query;

   public MerchantFromSearchEvent(String query) {
      this.query = query;
   }
}
