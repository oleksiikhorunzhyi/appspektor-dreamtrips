package com.worldventures.wallet.analytics;

import com.worldventures.janet.analytics.AnalyticsEvent;
import com.worldventures.core.service.analytics.Attribute;
import com.worldventures.core.service.analytics.AdobeTracker;

@AnalyticsEvent(action = "wallet:Card Detail",
                navigationState = true,
                trackers = AdobeTracker.TRACKER_KEY)
public class CardDetailsAction extends BaseCardDetailsWithDefaultAction {

   @Attribute("paycardnickname") final String cardNickname;

   public CardDetailsAction(String cardNickname) {
      this.cardNickname = cardNickname;
   }
}
