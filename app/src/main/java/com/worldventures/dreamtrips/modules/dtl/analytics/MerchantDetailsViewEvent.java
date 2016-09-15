package com.worldventures.dreamtrips.modules.dtl.analytics;

import com.innahema.collections.query.queriables.Queryable;
import com.worldventures.dreamtrips.core.utils.tracksystem.AdobeTracker;
import com.worldventures.dreamtrips.core.utils.tracksystem.AnalyticsEvent;
import com.worldventures.dreamtrips.core.utils.tracksystem.Attribute;
import com.worldventures.dreamtrips.modules.dtl.model.merchant.DtlMerchant;
import com.worldventures.dreamtrips.modules.dtl.model.merchant.offer.DtlOffer;

@AnalyticsEvent(action = "local:Restaurant-Listings:Merchant View",
                trackers = AdobeTracker.TRACKER_KEY)
public class MerchantDetailsViewEvent extends MerchantAnalyticsAction {

   @Attribute("areperksavail") final String perksAvailable;

   @Attribute("arepointsavail") final String pointsAvailable;

   @Attribute("numperks") final String perksNumber;

   @Attribute("offersonly") String isOffersOnly;

   public MerchantDetailsViewEvent(DtlMerchant dtlMerchant) {
      super(dtlMerchant);
      perksAvailable = dtlMerchant.hasPerks() ? "Yes" : "No";
      pointsAvailable = dtlMerchant.hasPoints() ? "Yes" : "No";
      perksNumber = String.valueOf(Queryable.from(dtlMerchant.getOffers()).count(DtlOffer::isPerk));
   }

   public void setOffersOnly(boolean isOffersOnly) {
      this.isOffersOnly = isOffersOnly ? "1" : null;
   }
}
