package com.worldventures.dreamtrips.modules.dtl.analytics;

import com.worldventures.dreamtrips.api.dtl.merchants.model.OfferType;
import com.worldventures.dreamtrips.core.utils.tracksystem.AdobeTracker;
import com.worldventures.dreamtrips.core.utils.tracksystem.AnalyticsEvent;
import com.worldventures.dreamtrips.core.utils.tracksystem.Attribute;
import com.worldventures.dreamtrips.modules.dtl.helper.inflater.MerchantAttributes;

@AnalyticsEvent(action = "local:Restaurant-Listings:Merchant View",
                trackers = AdobeTracker.TRACKER_KEY)
public class MerchantDetailsViewEvent extends MerchantAnalyticsAction {

   @Attribute("areperksavail") final String perksAvailable;

   @Attribute("arepointsavail") final String pointsAvailable;

   @Attribute("numperks") final String perksNumber;

   @Attribute("offersonly") String isOffersOnly;

   public MerchantDetailsViewEvent(MerchantAttributes merchantAttributes) {
      super(merchantAttributes);
      perksAvailable = merchantAttributes.hasPerks() ? "Yes" : "No";
      pointsAvailable = merchantAttributes.hasPoints() ? "Yes" : "No";
      perksNumber = String.valueOf(merchantAttributes.offersCount(OfferType.PERK));
   }

   public void setOffersOnly(boolean isOffersOnly) {
      this.isOffersOnly = isOffersOnly ? "1" : null;
   }
}
