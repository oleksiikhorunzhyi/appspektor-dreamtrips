package com.worldventures.wallet.analytics.new_smartcard

import com.worldventures.janet.analytics.AnalyticsEvent
import com.worldventures.core.service.analytics.Attribute
import com.worldventures.core.service.analytics.AdobeTracker
import com.worldventures.wallet.analytics.WalletAnalyticsAction

@AnalyticsEvent(action = "wallet:settings:general:setup new smartcard:existing card detected:card not connected:do not have card",
      trackers = arrayOf(AdobeTracker.TRACKER_KEY))
class ExistSmartCardDontHaveCardAction : WalletAnalyticsAction() {

   @Attribute("unassigncardstep2c") internal val unAssignCardStep2c = "1"
}