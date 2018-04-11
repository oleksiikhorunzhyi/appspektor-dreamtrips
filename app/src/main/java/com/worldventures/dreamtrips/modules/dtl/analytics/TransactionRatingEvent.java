package com.worldventures.dreamtrips.modules.dtl.analytics;

import com.worldventures.core.service.analytics.AdobeTracker;
import com.worldventures.core.service.analytics.Attribute;
import com.worldventures.dreamtrips.modules.dtl.helper.inflater.MerchantAttributes;
import com.worldventures.janet.analytics.AnalyticsEvent;

@AnalyticsEvent(action = "local:Restaurant-Listings:Merchant View:Congratulations:Rating",
                trackers = AdobeTracker.TRACKER_KEY)
public class TransactionRatingEvent extends MerchantAnalyticsAction {

   @Attribute("rateexperience") final String attribute = "1";

   public TransactionRatingEvent(MerchantAttributes merchantAttributes) {
      super(merchantAttributes);
   }
}
