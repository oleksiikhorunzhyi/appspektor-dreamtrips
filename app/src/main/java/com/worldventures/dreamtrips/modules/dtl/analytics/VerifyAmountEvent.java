package com.worldventures.dreamtrips.modules.dtl.analytics;

import com.worldventures.dreamtrips.core.utils.tracksystem.AdobeTracker;
import com.worldventures.dreamtrips.core.utils.tracksystem.AnalyticsEvent;
import com.worldventures.dreamtrips.core.utils.tracksystem.Attribute;
import com.worldventures.dreamtrips.modules.dtl.model.merchant.DtlMerchant;

import java.util.Locale;

@AnalyticsEvent(action = "local:Restaurant-Listings:Merchant View:Amount Verified",
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

    @Attribute("merchanttype")
    final String merchantType;

    @Attribute("amount_cc")
    final String currencyCode;

    public VerifyAmountEvent(DtlMerchant dtlMerchant, String currencyCode, Double enteredAmount) {
        this.enteredAmount = String.format(Locale.US, "%.2f", enteredAmount);
        merchantName = dtlMerchant.getDisplayName();
        merchantId = dtlMerchant.getId();
        merchantType = dtlMerchant.getType().toString();
        this.currencyCode = currencyCode;
    }
}
