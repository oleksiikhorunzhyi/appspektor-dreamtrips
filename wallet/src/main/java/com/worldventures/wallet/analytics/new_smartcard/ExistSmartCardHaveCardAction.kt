package com.worldventures.wallet.analytics.new_smartcard

import com.worldventures.janet.analytics.AnalyticsEvent
import com.worldventures.core.service.analytics.Attribute
import com.worldventures.core.service.analytics.AdobeTracker
import com.worldventures.wallet.analytics.WalletAnalyticsAction

@AnalyticsEvent(action = "wallet:settings:general:setup new smartcard:existing card detected:card not connected:power on smartcard",
      navigationState = true, trackers = arrayOf(AdobeTracker.TRACKER_KEY))
class ExistSmartCardHaveCardAction : WalletAnalyticsAction() {

   @Attribute("unassigncardstep2b") internal val unAssignCardStep2b = "1"
}
