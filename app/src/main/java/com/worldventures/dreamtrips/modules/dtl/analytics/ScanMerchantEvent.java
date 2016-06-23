package com.worldventures.dreamtrips.modules.dtl.analytics;

import com.worldventures.dreamtrips.core.utils.tracksystem.AdobeTracker;
import com.worldventures.dreamtrips.core.utils.tracksystem.Analytics;
import com.worldventures.dreamtrips.core.utils.tracksystem.Attribute;
import com.worldventures.dreamtrips.modules.dtl.model.merchant.DtlMerchant;

@Analytics(action = "local:Restaurant-Listings:Merchant View:QR Scan",
        trackers = AdobeTracker.TRACKER_KEY)
public class ScanMerchantEvent extends DtlAnalyticsAction {

    @Attribute("scan")
    final String attribute = "1";

    @Attribute("merchantID")
    final String merchantId;

    @Attribute("merchantname")
    final String merchantName;

    @Attribute("scan_id")
    final String merchantToken;

    public ScanMerchantEvent(DtlMerchant dtlMerchant,
                             String merchantToken) {
        this.merchantToken = merchantToken;
        merchantId = dtlMerchant.getId();
        merchantName = dtlMerchant.getDisplayName();
    }
}
