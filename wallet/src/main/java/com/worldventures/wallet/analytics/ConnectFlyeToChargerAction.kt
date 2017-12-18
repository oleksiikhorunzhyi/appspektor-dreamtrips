package com.worldventures.wallet.analytics

import com.worldventures.core.service.analytics.AdobeTracker
import com.worldventures.core.service.analytics.Attribute
import com.worldventures.janet.analytics.AnalyticsEvent

@AnalyticsEvent(action = "wallet:Add a Card:Connect Flye Card to Charger", navigationState = true, trackers = arrayOf(AdobeTracker.TRACKER_KEY))
class ConnectFlyeToChargerAction : WalletAnalyticsAction() {

   @Attribute("cardtype") internal val cardType = "Payment"
}
