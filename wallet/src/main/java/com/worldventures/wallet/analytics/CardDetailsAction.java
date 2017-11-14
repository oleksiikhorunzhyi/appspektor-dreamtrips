package com.worldventures.wallet.analytics;

import com.worldventures.core.service.analytics.AdobeTracker;
import com.worldventures.core.service.analytics.Attribute;
import com.worldventures.janet.analytics.AnalyticsEvent;

@AnalyticsEvent(action = "wallet:Card Detail",
                navigationState = true,
                trackers = AdobeTracker.TRACKER_KEY)
public class CardDetailsAction extends BaseCardDetailsWithDefaultAction {

   @Attribute("paycardnickname") final String cardNickname;

   public CardDetailsAction(String cardNickname) {
      this.cardNickname = cardNickname;
   }
}
