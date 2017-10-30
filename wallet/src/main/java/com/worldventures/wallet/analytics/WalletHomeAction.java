package com.worldventures.wallet.analytics;

import com.worldventures.core.service.analytics.AdobeTracker;
import com.worldventures.core.service.analytics.Attribute;
import com.worldventures.janet.analytics.AnalyticsEvent;

@AnalyticsEvent(action = "wallet:Wallet Home",
                navigationState = true,
                trackers = AdobeTracker.TRACKER_KEY)
public class WalletHomeAction extends WalletAnalyticsAction {

   @Attribute("numofcards") int numOfCards;

   public WalletHomeAction(int numOfCards) {
      this.numOfCards = numOfCards;
   }
}
