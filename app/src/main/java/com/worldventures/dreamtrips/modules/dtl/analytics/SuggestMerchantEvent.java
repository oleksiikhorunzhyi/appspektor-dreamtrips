package com.worldventures.dreamtrips.modules.dtl.analytics;

import com.worldventures.dreamtrips.core.utils.tracksystem.AdobeTracker;
import com.worldventures.dreamtrips.core.utils.tracksystem.AnalyticsEvent;
import com.worldventures.dreamtrips.core.utils.tracksystem.Attribute;
import com.worldventures.dreamtrips.modules.dtl.model.merchant.DtlMerchant;

@AnalyticsEvent(action = "local:Restaurant-Listings:Merchant View:Suggest Merchant",
                trackers = AdobeTracker.TRACKER_KEY)
public class SuggestMerchantEvent extends MerchantAnalyticsAction {

   @Attribute("suggestmerchant") final String attribute = "1";

   public SuggestMerchantEvent(DtlMerchant dtlMerchant) {
      super(dtlMerchant);
   }
}
