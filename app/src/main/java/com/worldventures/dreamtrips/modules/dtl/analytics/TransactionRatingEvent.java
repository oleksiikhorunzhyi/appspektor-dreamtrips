package com.worldventures.dreamtrips.modules.dtl.analytics;

import com.worldventures.core.service.analytics.AnalyticsEvent;
import com.worldventures.core.service.analytics.Attribute;
import com.worldventures.core.service.analytics.AdobeTracker;
import com.worldventures.dreamtrips.modules.dtl.helper.inflater.MerchantAttributes;

@AnalyticsEvent(action = "local:Restaurant-Listings:Merchant View:Congratulations:Rating",
                trackers = AdobeTracker.TRACKER_KEY)
public class TransactionRatingEvent extends MerchantAnalyticsAction {

   @Attribute("rateexperience") final String attribute = "1";

   @Attribute("ratedexperience") final String rating;

   public TransactionRatingEvent(MerchantAttributes merchantAttributes, int rating) {
      super(merchantAttributes);
      this.rating = String.valueOf(rating);
   }
}
