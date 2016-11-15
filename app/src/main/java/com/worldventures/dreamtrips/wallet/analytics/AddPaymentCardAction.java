package com.worldventures.dreamtrips.wallet.analytics;

import com.worldventures.dreamtrips.core.utils.tracksystem.AdobeTracker;
import com.worldventures.dreamtrips.core.utils.tracksystem.AnalyticsEvent;
import com.worldventures.dreamtrips.core.utils.tracksystem.Attribute;

@AnalyticsEvent(action = "wallet:Add a Card:Add Payment Card",
                trackers = AdobeTracker.TRACKER_KEY)
public class AddPaymentCardAction extends WalletAnalyticsAction {

   @Attribute("addcard") final String addCard = "1";
   @Attribute("cardtype") final String cardType = "Payment";

   public AddPaymentCardAction() {
   }
}
