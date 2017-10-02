package com.worldventures.dreamtrips.wallet.analytics;

import com.worldventures.core.service.analytics.AnalyticsEvent;
import com.worldventures.core.service.analytics.Attribute;
import com.worldventures.core.service.analytics.AdobeTracker;

@AnalyticsEvent(action = "wallet:Add a Card:Connect Flye Card to Charger",
                navigationState = true,
                trackers = AdobeTracker.TRACKER_KEY)
public class ConnectFlyeToChargerAction extends WalletAnalyticsAction {

   @Attribute("cardtype") final String cardType = "Payment";

   public ConnectFlyeToChargerAction() {
   }
}
