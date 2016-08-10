package com.worldventures.dreamtrips.modules.dtl.analytics;

import com.innahema.collections.query.queriables.Queryable;
import com.worldventures.dreamtrips.core.utils.tracksystem.AdobeTracker;
import com.worldventures.dreamtrips.core.utils.tracksystem.AnalyticsEvent;
import com.worldventures.dreamtrips.core.utils.tracksystem.Attribute;
import com.worldventures.dreamtrips.modules.dtl.model.merchant.DtlMerchant;
import com.worldventures.dreamtrips.modules.dtl.model.merchant.offer.DtlOffer;

@AnalyticsEvent(action = "local:Restaurant-Listings:Offer View", trackers = AdobeTracker.TRACKER_KEY)
public class MerchantsListingExpandEvent extends DtlAnalyticsAction {

    @Attribute("merchantname")
    final String merchantName;

    @Attribute("merchantID")
    final String merchantId;

    @Attribute("offerview")
    final String attribute = "1";

    @Attribute("numperks")
    final String perksNumber;

    @Attribute("areperksavail")
    final String perksAvailable;

    @Attribute("arepointsavail")
    final String pointsAvailable;

    public MerchantsListingExpandEvent(DtlMerchant dtlMerchant) {
        merchantId = dtlMerchant.getId();
        merchantName = dtlMerchant.getDisplayName();
        perksAvailable = dtlMerchant.hasPerks() ? "Yes" : "No";
        pointsAvailable = dtlMerchant.hasPoints() ? "Yes" : "No";
        perksNumber = String.valueOf(Queryable.from(dtlMerchant.getOffers())
                .count(DtlOffer::isPerk));
    }
}