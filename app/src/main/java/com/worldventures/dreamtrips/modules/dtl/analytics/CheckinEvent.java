package com.worldventures.dreamtrips.modules.dtl.analytics;

import com.worldventures.dreamtrips.core.utils.tracksystem.AdobeTracker;
import com.worldventures.dreamtrips.core.utils.tracksystem.AnalyticsEvent;
import com.worldventures.dreamtrips.core.utils.tracksystem.Attribute;
import com.worldventures.dreamtrips.modules.dtl.model.merchant.DtlMerchant;

@AnalyticsEvent(action = "local:Restaurant-Listings:Merchant View:Check-In",
        trackers = AdobeTracker.TRACKER_KEY)
public class CheckinEvent extends MerchantAnalyticsAction {

    @Attribute("checkin")
    final String attribute = "1";

    @Attribute("areperksavail")
    final String perksAvailable;

    @Attribute("arepointsavail")
    final String pointsAvailable;

    public CheckinEvent(DtlMerchant merchant) {
        super(merchant);
        perksAvailable = merchant.hasPerks() ? "Yes" : "No";
        pointsAvailable = merchant.hasPoints() ? "Yes" : "No";
    }
}
