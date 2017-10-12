package com.worldventures.dreamtrips.wallet.analytics;

import com.worldventures.core.service.analytics.AnalyticsEvent;
import com.worldventures.core.service.analytics.Attribute;
import com.worldventures.core.service.analytics.AdobeTracker;

@AnalyticsEvent(action = "wallet:Add a Card:Error Adding",
                trackers = AdobeTracker.TRACKER_KEY)
public final class FailedToAddCardAction extends WalletAnalyticsAction {

   @Attribute("addcarderror") final String addCardError = "1";
   @Attribute("cardtype") final String cardType = "Payment";
   @Attribute("adderrortype") final String errorType;

   private FailedToAddCardAction(String errorType) {
      this.errorType = errorType;
   }

   public static FailedToAddCardAction noCardConnection() {
      return new FailedToAddCardAction("No Card Connection");
   }

   public static FailedToAddCardAction noNetworkConnection() {
      return new FailedToAddCardAction("No Network Connection");
   }

}
