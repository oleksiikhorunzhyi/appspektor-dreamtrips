package com.worldventures.dreamtrips.modules.dtl.analytics;

import com.worldventures.dreamtrips.core.utils.tracksystem.AdobeTracker;
import com.worldventures.dreamtrips.core.utils.tracksystem.AnalyticsEvent;
import com.worldventures.dreamtrips.core.utils.tracksystem.Attribute;
import com.worldventures.dreamtrips.modules.dtl.model.merchant.DtlMerchant;

@AnalyticsEvent(action = "local:Restaurant-Listings:Merchant View:Suggest Merchant",
        trackers = AdobeTracker.TRACKER_KEY)
public class SuggestMerchantEvent extends DtlAnalyticsAction {

    @Attribute("suggestmerchant")
    final String attribute = "1";

    @Attribute("merchantname")
    final String merchantName;

    @Attribute("merchantID")
    final String merchantId;

    public SuggestMerchantEvent(DtlMerchant dtlMerchant) {
        merchantId = dtlMerchant.getId();
        merchantName = dtlMerchant.getDisplayName();
    }
}
