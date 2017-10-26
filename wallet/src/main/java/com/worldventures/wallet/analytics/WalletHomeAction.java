package com.worldventures.wallet.analytics;

import com.worldventures.janet.analytics.AnalyticsEvent;
import com.worldventures.core.service.analytics.Attribute;
import com.worldventures.core.service.analytics.AdobeTracker;

@AnalyticsEvent(action = "wallet:Wallet Home",
                navigationState = true,
                trackers = AdobeTracker.TRACKER_KEY)
public class WalletHomeAction extends WalletAnalyticsAction {

   @Attribute("numofcards") int numOfCards;

   public WalletHomeAction(int numOfCards) {
      this.numOfCards = numOfCards;
   }
}
