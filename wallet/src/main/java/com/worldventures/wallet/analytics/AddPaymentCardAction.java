package com.worldventures.wallet.analytics;

import com.worldventures.core.service.analytics.AdobeTracker;
import com.worldventures.core.service.analytics.Attribute;
import com.worldventures.janet.analytics.AnalyticsEvent;

@AnalyticsEvent(action = "wallet:Add a Card:Add Payment Card",
                trackers = AdobeTracker.TRACKER_KEY)
public class AddPaymentCardAction extends WalletAnalyticsAction {

   @Attribute("addcard") final String addCard = "1";
   @Attribute("cardtype") final String cardType = "Payment";

   public AddPaymentCardAction() {
   }
}
