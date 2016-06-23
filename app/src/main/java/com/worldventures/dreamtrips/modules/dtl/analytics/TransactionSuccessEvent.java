package com.worldventures.dreamtrips.modules.dtl.analytics;

import com.worldventures.dreamtrips.core.utils.tracksystem.AdobeTracker;
import com.worldventures.dreamtrips.core.utils.tracksystem.AnalyticsEvent;
import com.worldventures.dreamtrips.core.utils.tracksystem.Attribute;
import com.worldventures.dreamtrips.modules.dtl.model.merchant.DtlMerchant;
import com.worldventures.dreamtrips.modules.dtl.model.transaction.DtlTransaction;

@AnalyticsEvent(action = "local:Restaurant-Listings:Merchant View:Congratulations",
        trackers = AdobeTracker.TRACKER_KEY)
public class TransactionSuccessEvent extends DtlAnalyticsAction {

    @Attribute("merchantname")
    final String merchantName;

    @Attribute("merchantID")
    final String merchantId;

    @Attribute("merchantearned")
    final String attribute = "1";

    @Attribute("localpointsearned")
    final String earnedAmount;

    @Attribute("completedamount")
    final String spentAmount;

    @Attribute("amount_cc")
    final String currencyCode;

    @Attribute("areperksavail")
    final String perksAvailable;

    @Attribute("arepointsavail")
    final String pointsAvailable;

    public TransactionSuccessEvent(DtlMerchant dtlMerchant, DtlTransaction dtlTransaction) {
        merchantId = dtlMerchant.getId();
        merchantName = dtlMerchant.getDisplayName();
        perksAvailable = dtlMerchant.hasPerks() ? "Yes" : "No";
        pointsAvailable = dtlMerchant.hasPoints() ? "Yes" : "No";
        currencyCode = dtlMerchant.getDefaultCurrency().getCode();
        earnedAmount = String.valueOf(Math.round(
                dtlTransaction.getDtlTransactionResult().getEarnedPoints()));
        spentAmount = String.valueOf(Math.round(dtlTransaction.getBillTotal()));
    }
}
