package com.worldventures.dreamtrips.wallet.analytics;

import com.worldventures.dreamtrips.core.utils.tracksystem.AdobeTracker;
import com.worldventures.dreamtrips.core.utils.tracksystem.AnalyticsEvent;
import com.worldventures.dreamtrips.core.utils.tracksystem.Attribute;

@AnalyticsEvent(action = "wallet:Add a Card:Connect Flye Card to Charger",
                trackers = AdobeTracker.TRACKER_KEY)
public class ConnectFlyeToChargerAction extends WalletAnalyticsAction {

   @Attribute("cardtype") final String cardType = "Payment";

   public ConnectFlyeToChargerAction() {
   }
}
