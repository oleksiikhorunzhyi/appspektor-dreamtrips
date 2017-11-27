package com.worldventures.wallet.analytics.new_smartcard

import com.worldventures.janet.analytics.AnalyticsEvent
import com.worldventures.core.service.analytics.Attribute
import com.worldventures.core.service.analytics.AdobeTracker
import com.worldventures.wallet.analytics.WalletAnalyticsAction

@AnalyticsEvent(action = "wallet:settings:general:setup new smartcard:existing card detected:card connected:unassign card",
      trackers = arrayOf(AdobeTracker.TRACKER_KEY))
class UnAssignCardContinueAction : WalletAnalyticsAction() {

   @Attribute("unassigncardstep3a") internal val unAssignCardStep3a = "1"
}
