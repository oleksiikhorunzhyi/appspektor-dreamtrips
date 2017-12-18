package com.worldventures.wallet.analytics.wizard

import com.worldventures.janet.analytics.AnalyticsEvent
import com.worldventures.core.service.analytics.Attribute
import com.worldventures.core.service.analytics.AdobeTracker
import com.worldventures.wallet.analytics.WalletAnalyticsAction

@AnalyticsEvent(action = "wallet:setup:Step 7:Your PIN is set", navigationState = true, trackers = arrayOf(AdobeTracker.TRACKER_KEY))
class PinWasSetAction : WalletAnalyticsAction() {

   @Attribute("cardsetupstep7") internal val cardsetupstep7 = "1"
}
