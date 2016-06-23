package com.worldventures.dreamtrips.modules.dtl.analytics;

import com.worldventures.dreamtrips.core.utils.tracksystem.AdobeTracker;
import com.worldventures.dreamtrips.core.utils.tracksystem.Analytics;
import com.worldventures.dreamtrips.core.utils.tracksystem.Attribute;
import com.worldventures.dreamtrips.modules.dtl.model.merchant.DtlMerchant;

@Analytics(action = "local:Restaurant-Listings:Merchant View:Receipt Capture",
        trackers = AdobeTracker.TRACKER_KEY)
public class CaptureReceiptEvent extends DtlAnalyticsAction {

    @Attribute("capture")
    final String attribute = "1";

    @Attribute("merchantname")
    final String merchantName;

    @Attribute("merchantID")
    final String merchantId;

    public CaptureReceiptEvent(DtlMerchant dtlMerchant) {
        merchantId = dtlMerchant.getId();
        merchantName = dtlMerchant.getDisplayName();
    }
}
