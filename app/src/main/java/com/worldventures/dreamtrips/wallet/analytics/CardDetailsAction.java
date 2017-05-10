package com.worldventures.dreamtrips.wallet.analytics;

import com.worldventures.dreamtrips.core.utils.tracksystem.AdobeTracker;
import com.worldventures.dreamtrips.core.utils.tracksystem.AnalyticsEvent;
import com.worldventures.dreamtrips.core.utils.tracksystem.Attribute;

@AnalyticsEvent(action = "wallet:Card Detail",
                navigationState = true,
                trackers = AdobeTracker.TRACKER_KEY)
public class CardDetailsAction extends BaseCardDetailsWithDefaultAction {

   @Attribute("paycardnickname") final String cardNickname;

   public CardDetailsAction(String cardNickname) {
      this.cardNickname = cardNickname;
   }
}
