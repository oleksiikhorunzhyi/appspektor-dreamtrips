package com.worldventures.dreamtrips.wallet.analytics;

import com.worldventures.dreamtrips.core.utils.tracksystem.AdobeTracker;
import com.worldventures.dreamtrips.core.utils.tracksystem.AnalyticsEvent;
import com.worldventures.dreamtrips.core.utils.tracksystem.Attribute;

@AnalyticsEvent(action = "wallet:Wallet Home",
                navigationState = true,
                trackers = AdobeTracker.TRACKER_KEY)
public class WalletHomeAction extends WalletAnalyticsAction {

   @Attribute("numofcards") int numOfCards;

   public WalletHomeAction(int numOfCards) {
      this.numOfCards = numOfCards;
   }
}
