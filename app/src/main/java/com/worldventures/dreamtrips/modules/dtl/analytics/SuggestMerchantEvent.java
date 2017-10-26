package com.worldventures.dreamtrips.modules.dtl.analytics;

import com.worldventures.janet.analytics.AnalyticsEvent;
import com.worldventures.core.service.analytics.Attribute;
import com.worldventures.core.service.analytics.AdobeTracker;
import com.worldventures.dreamtrips.modules.dtl.helper.inflater.MerchantAttributes;

@AnalyticsEvent(action = "local:Restaurant-Listings:Merchant View:Suggest Merchant",
                trackers = AdobeTracker.TRACKER_KEY)
public class SuggestMerchantEvent extends MerchantAnalyticsAction {

   @Attribute("suggestmerchant") final String attribute = "1";

   public SuggestMerchantEvent(MerchantAttributes merchantAttributes) {
      super(merchantAttributes);
   }
}
