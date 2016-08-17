package com.worldventures.dreamtrips.modules.dtl.analytics;

import com.worldventures.dreamtrips.core.utils.tracksystem.AdobeTracker;
import com.worldventures.dreamtrips.core.utils.tracksystem.AnalyticsEvent;
import com.worldventures.dreamtrips.core.utils.tracksystem.Attribute;
import com.worldventures.dreamtrips.modules.dtl.model.merchant.DtlMerchant;

@AnalyticsEvent(action = "local:Restaurant-Listings:Merchant View:Congratulations:Rating",
                trackers = AdobeTracker.TRACKER_KEY)
public class TransactionRatingEvent extends MerchantAnalyticsAction {

   @Attribute("rateexperience") final String attribute = "1";

   @Attribute("ratedexperience") final String rating;

   public TransactionRatingEvent(DtlMerchant dtlMerchant, int rating) {
      super(dtlMerchant);
      this.rating = String.valueOf(rating);
   }
}
