package com.worldventures.wallet.analytics.new_smartcard

import com.worldventures.janet.analytics.AnalyticsEvent
import com.worldventures.core.service.analytics.Attribute
import com.worldventures.core.service.analytics.AdobeTracker
import com.worldventures.wallet.analytics.WalletAnalyticsAction

@AnalyticsEvent(action = "wallet:settings:general:setup new smartcard:existing card detected:card not connected:do not have card:complete unassign",
      trackers = arrayOf(AdobeTracker.TRACKER_KEY))
class ExistSmartCardDontHaveCardContinueAction : WalletAnalyticsAction() {

   @Attribute("unassigncardstep3c") internal val unAssignCardStep3c = "1"
}