package com.worldventures.dreamtrips.modules.dtl.analytics;

import com.worldventures.dreamtrips.core.utils.tracksystem.AdobeTracker;
import com.worldventures.dreamtrips.core.utils.tracksystem.Analytics;
import com.worldventures.dreamtrips.core.utils.tracksystem.Attribute;
import com.worldventures.dreamtrips.core.utils.tracksystem.BaseAnalyticsAction;
import com.worldventures.dreamtrips.core.utils.tracksystem.TrackingHelper;
import com.worldventures.dreamtrips.modules.dtl.model.merchant.DtlMerchant;

@Analytics(action = TrackingHelper.DTL_ACTION_CHECKIN, trackers = AdobeTracker.TRACKER_KEY)
public class CheckinEvent extends BaseAnalyticsAction {

    @Attribute(TrackingHelper.DTL_ATTRIBUTE_MERCHANT)
    String merchantId;

    public CheckinEvent(DtlMerchant merchant) {
        this.merchantId = merchant.getId();
    }
}
