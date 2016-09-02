package com.worldventures.dreamtrips.modules.dtl.analytics;

import android.location.Location;

import com.worldventures.dreamtrips.core.utils.tracksystem.AdobeTracker;
import com.worldventures.dreamtrips.core.utils.tracksystem.AnalyticsEvent;
import com.worldventures.dreamtrips.core.utils.tracksystem.Attribute;
import com.worldventures.dreamtrips.modules.dtl.model.merchant.DtlMerchant;
import com.worldventures.dreamtrips.modules.dtl.model.transaction.DtlTransaction;

import java.util.Locale;

@AnalyticsEvent(action = "local:Restaurant-Listings:Merchant View:Congratulations",
                trackers = AdobeTracker.TRACKER_KEY)
public class TransactionSuccessEvent extends MerchantAnalyticsAction {

   @Attribute("merchantearned") final String attribute = "1";

   @Attribute("localpointsearned") final String earnedAmount;

   @Attribute("completedamount") final String spentAmount;

   @Attribute("amount_cc") final String currencyCode;

   @Attribute("areperksavail") final String perksAvailable;

   @Attribute("arepointsavail") final String pointsAvailable;

   @Attribute("coordinates") final String coordinates;

   public TransactionSuccessEvent(DtlMerchant dtlMerchant, DtlTransaction dtlTransaction, Location location) {
      super(dtlMerchant);
      perksAvailable = dtlMerchant.hasPerks() ? "Yes" : "No";
      pointsAvailable = dtlMerchant.hasPoints() ? "Yes" : "No";
      currencyCode = dtlMerchant.getDefaultCurrency().code();
      earnedAmount = String.format(Locale.US, "%.0f", dtlTransaction.getDtlTransactionResult().getEarnedPoints());
      spentAmount = String.format(Locale.US, "%.2f", dtlTransaction.getBillTotal());
      coordinates = String.format(Locale.US, "%f,%f", location.getLatitude(), location.getLongitude());
   }
}
