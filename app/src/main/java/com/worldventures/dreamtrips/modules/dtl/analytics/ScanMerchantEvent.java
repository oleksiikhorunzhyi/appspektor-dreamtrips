package com.worldventures.dreamtrips.modules.dtl.analytics;

import com.worldventures.dreamtrips.core.utils.tracksystem.AdobeTracker;
import com.worldventures.dreamtrips.core.utils.tracksystem.AnalyticsEvent;
import com.worldventures.dreamtrips.core.utils.tracksystem.Attribute;
import com.worldventures.dreamtrips.modules.dtl.model.merchant.DtlMerchant;

@AnalyticsEvent(action = "local:Restaurant-Listings:Merchant View:QR Scan",
        trackers = AdobeTracker.TRACKER_KEY)
public class ScanMerchantEvent extends MerchantAnalyticsAction {

    @Attribute("scan")
    final String attribute = "1";

    @Attribute("scan_id")
    final String merchantToken;

    public ScanMerchantEvent(DtlMerchant dtlMerchant, String merchantToken) {
        super(dtlMerchant);
        this.merchantToken = merchantToken;
    }
}
