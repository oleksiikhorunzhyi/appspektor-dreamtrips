package com.worldventures.dreamtrips.wallet.analytics;

import com.worldventures.core.service.analytics.AdobeTracker;
import com.worldventures.core.service.analytics.AnalyticsEvent;
import com.worldventures.core.service.analytics.Attribute;

@AnalyticsEvent(action = "wallet:Card Detail",
                navigationState = true,
                trackers = AdobeTracker.TRACKER_KEY)
public class CardDetailsAction extends BaseCardDetailsWithDefaultAction {

   @Attribute("paycardnickname") final String cardNickname;

   public CardDetailsAction(String cardNickname) {
      this.cardNickname = cardNickname;
   }
}
