package com.worldventures.dreamtrips.modules.dtl.analytics;

import com.innahema.collections.query.queriables.Queryable;
import com.worldventures.dreamtrips.api.dtl.merchants.model.OfferType;
import com.worldventures.dreamtrips.core.utils.tracksystem.AdobeTracker;
import com.worldventures.dreamtrips.core.utils.tracksystem.AnalyticsEvent;
import com.worldventures.dreamtrips.core.utils.tracksystem.Attribute;
import com.worldventures.dreamtrips.modules.dtl.helper.inflater.MerchantAttributes;
import com.worldventures.dreamtrips.modules.dtl.model.merchant.DtlMerchant;

@AnalyticsEvent(action = "local:Restaurant-Listings:Offer View", trackers = AdobeTracker.TRACKER_KEY)
public class MerchantsListingExpandEvent extends MerchantAnalyticsAction {

   @Attribute("offerview") final String attribute = "1";

   @Attribute("numperks") final String perksNumber;

   @Attribute("areperksavail") final String perksAvailable;

   @Attribute("arepointsavail") final String pointsAvailable;

   public MerchantsListingExpandEvent(MerchantAttributes merchantAttributes) {
      super(merchantAttributes);
      perksAvailable = merchantAttributes.hasPerks() ? "Yes" : "No";
      pointsAvailable = merchantAttributes.hasPoints() ? "Yes" : "No";
      perksNumber = String.valueOf(merchantAttributes.offersCount(OfferType.PERK));
   }

   public MerchantsListingExpandEvent(DtlMerchant merchant) {
      super(merchant);
      perksAvailable = merchant.hasPerks() ? "Yes" : "No";
      pointsAvailable = merchant.hasPoints() ? "Yes" : "No";
      perksNumber = String.valueOf(Queryable.from(merchant.getOffers()).count(offer->offer.type() == OfferType.PERK));
   }
}
