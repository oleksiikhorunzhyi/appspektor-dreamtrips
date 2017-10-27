package com.worldventures.wallet.analytics.new_smartcard

import com.worldventures.janet.analytics.AnalyticsEvent
import com.worldventures.core.service.analytics.Attribute
import com.worldventures.core.service.analytics.AdobeTracker
import com.worldventures.wallet.analytics.WalletAnalyticsAction

@AnalyticsEvent(action = "wallet:settings:general:setup new smartcard:existing card detected:card connected:unassign card:enter pin",
      navigationState = true, trackers = arrayOf(AdobeTracker.TRACKER_KEY))
class EnterPinUnAssignAction : WalletAnalyticsAction() {

   @Attribute("unassigncardstep4") internal val unAssignCardStep4 = "1"
}