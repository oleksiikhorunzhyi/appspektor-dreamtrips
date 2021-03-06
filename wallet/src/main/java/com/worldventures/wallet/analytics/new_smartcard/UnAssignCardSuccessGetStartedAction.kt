package com.worldventures.wallet.analytics.new_smartcard

import com.worldventures.janet.analytics.AnalyticsEvent
import com.worldventures.core.service.analytics.Attribute
import com.worldventures.core.service.analytics.AdobeTracker
import com.worldventures.wallet.analytics.WalletAnalyticsAction

@AnalyticsEvent(action = "wallet:settings:general:setup new smartcard:existing card detected:unassign successful:get started",
      trackers = arrayOf(AdobeTracker.TRACKER_KEY))
class UnAssignCardSuccessGetStartedAction : WalletAnalyticsAction() {

   @Attribute("unassigncardstep7") internal val unAssignCardStep7 = "1"
}
