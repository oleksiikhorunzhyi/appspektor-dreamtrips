package com.worldventures.wallet.analytics.wizard

import com.worldventures.janet.analytics.AnalyticsEvent
import com.worldventures.core.service.analytics.Attribute
import com.worldventures.core.service.analytics.AdobeTracker
import com.worldventures.wallet.analytics.WalletAnalyticsAction

@AnalyticsEvent(action = "wallet:setup:Step 3:Card Successfully Connected", trackers = arrayOf(AdobeTracker.TRACKER_KEY))
class CardConnectedAction(smartCardId: String) : WalletAnalyticsAction(smartCardId) {

   @Attribute("cardsetupstep3") internal val cardSetupStep3 = "1"
}
