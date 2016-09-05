package com.worldventures.dreamtrips.modules.dtl.analytics;

import com.innahema.collections.query.queriables.Queryable;
import com.worldventures.dreamtrips.api.dtl.merchants.model.OfferType;
import com.worldventures.dreamtrips.core.utils.tracksystem.AdobeTracker;
import com.worldventures.dreamtrips.core.utils.tracksystem.AnalyticsEvent;
import com.worldventures.dreamtrips.core.utils.tracksystem.Attribute;
import com.worldventures.dreamtrips.modules.dtl.model.merchant.DtlMerchant;

@AnalyticsEvent(action = "local:Restaurant-Listings:Offer View", trackers = AdobeTracker.TRACKER_KEY)
public class MerchantsListingExpandEvent extends MerchantAnalyticsAction {

   @Attribute("offerview") final String attribute = "1";

   @Attribute("numperks") final String perksNumber;

   @Attribute("areperksavail") final String perksAvailable;

   @Attribute("arepointsavail") final String pointsAvailable;

   public MerchantsListingExpandEvent(DtlMerchant dtlMerchant) {
      super(dtlMerchant);
      perksAvailable = dtlMerchant.hasPerks() ? "Yes" : "No";
      pointsAvailable = dtlMerchant.hasPoints() ? "Yes" : "No";
      perksNumber = String.valueOf(Queryable.from(dtlMerchant.getOffers()).count(offer->offer.type() == OfferType.PERK));
   }
}
