package com.worldventures.dreamtrips.modules.dtl.analytics;

import com.worldventures.dreamtrips.core.utils.tracksystem.AdobeTracker;
import com.worldventures.dreamtrips.core.utils.tracksystem.AnalyticsEvent;
import com.worldventures.dreamtrips.core.utils.tracksystem.Attribute;
import com.worldventures.dreamtrips.modules.dtl.model.merchant.DtlMerchant;

import java.util.Locale;

@AnalyticsEvent(action = "local:Restaurant-Listings:Merchant View:Amount Verified",
                trackers = AdobeTracker.TRACKER_KEY)
public class VerifyAmountEvent extends MerchantAnalyticsAction {

   @Attribute("verify") final String attribute = "1";

   @Attribute("amount_id") final String enteredAmount;

   @Attribute("amount_cc") final String currencyCode;

   public VerifyAmountEvent(DtlMerchant dtlMerchant, String currencyCode, Double enteredAmount) {
      super(dtlMerchant);
      this.enteredAmount = String.format(Locale.US, "%.2f", enteredAmount);
      this.currencyCode = currencyCode;
   }
}
