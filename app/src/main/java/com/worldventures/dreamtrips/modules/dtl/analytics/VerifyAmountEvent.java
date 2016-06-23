package com.worldventures.dreamtrips.modules.dtl.analytics;

import com.worldventures.dreamtrips.core.utils.tracksystem.AdobeTracker;
import com.worldventures.dreamtrips.core.utils.tracksystem.Analytics;
import com.worldventures.dreamtrips.core.utils.tracksystem.Attribute;
import com.worldventures.dreamtrips.modules.dtl.model.merchant.DtlMerchant;

@Analytics(action = "local:Restaurant-Listings:Merchant View:Amount Verified",
        trackers = AdobeTracker.TRACKER_KEY)
public class VerifyAmountEvent extends DtlAnalyticsAction {

    @Attribute("verify")
    final String attribute = "1";

    @Attribute("amount_id")
    final String enteredAmount;

    @Attribute("merchantname")
    final String merchantName;

    @Attribute("merchantID")
    final String merchantId;

    @Attribute("amount_cc")
    final String currencyCode;

    public VerifyAmountEvent(DtlMerchant dtlMerchant, String currencyCode,
                             String enteredAmount) {
        this.enteredAmount = enteredAmount;
        merchantName = dtlMerchant.getDisplayName();
        merchantId = dtlMerchant.getId();
        this.currencyCode = currencyCode;
    }
}
