package com.worldventures.dreamtrips.modules.dtl.analytics;

import com.worldventures.core.service.analytics.AdobeTracker;
import com.worldventures.core.service.analytics.Attribute;
import com.worldventures.dreamtrips.api.dtl.merchants.model.OfferType;
import com.worldventures.dreamtrips.modules.dtl.helper.inflater.MerchantAttributes;
import com.worldventures.janet.analytics.AnalyticsEvent;

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
}
