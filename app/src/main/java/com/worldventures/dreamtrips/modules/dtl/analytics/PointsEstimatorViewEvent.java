package com.worldventures.dreamtrips.modules.dtl.analytics;

import com.worldventures.dreamtrips.core.utils.tracksystem.AdobeTracker;
import com.worldventures.dreamtrips.core.utils.tracksystem.AnalyticsEvent;
import com.worldventures.dreamtrips.core.utils.tracksystem.Attribute;
import com.worldventures.dreamtrips.modules.dtl.model.merchant.DtlMerchant;

@AnalyticsEvent(action = "local:Restaurant-Listings:Merchant View:Point Estimator",
        trackers = AdobeTracker.TRACKER_KEY)
public class PointsEstimatorViewEvent extends DtlAnalyticsAction {

    @Attribute("pointest")
    final String attribute = "1";

    @Attribute("merchantname")
    final String merchantName;

    @Attribute("merchantID")
    final String merchantId;

    @Attribute("merchanttype")
    final String merchantType;

    public PointsEstimatorViewEvent(DtlMerchant dtlMerchant) {
        merchantName = dtlMerchant.getDisplayName();
        merchantId = dtlMerchant.getId();
        merchantType = dtlMerchant.getType().toString();
    }
}
