package com.worldventures.dreamtrips.modules.dtl.analytics;

import com.worldventures.dreamtrips.core.utils.tracksystem.AdobeTracker;
import com.worldventures.dreamtrips.core.utils.tracksystem.AnalyticsEvent;
import com.worldventures.dreamtrips.core.utils.tracksystem.Attribute;
import com.worldventures.dreamtrips.modules.dtl.model.merchant.DtlMerchant;

@AnalyticsEvent(action = "local:Restaurant-Listings:Merchant View:Calculate Points",
        trackers = AdobeTracker.TRACKER_KEY)
public class PointsEstimatorCalculateEvent extends DtlAnalyticsAction {

    @Attribute("merchantname")
    final String merchantName;

    @Attribute("merchantID")
    final String merchantId;

    @Attribute("calcpoints")
    final String attribute = "1";

    public PointsEstimatorCalculateEvent(DtlMerchant dtlMerchant) {
        merchantName = dtlMerchant.getDisplayName();
        merchantId = dtlMerchant.getId();
    }
}
