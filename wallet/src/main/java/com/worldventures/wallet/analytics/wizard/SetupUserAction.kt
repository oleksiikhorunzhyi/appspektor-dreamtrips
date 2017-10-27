package com.worldventures.wallet.analytics.wizard

import com.worldventures.janet.analytics.AnalyticsEvent
import com.worldventures.core.service.analytics.Attribute
import com.worldventures.core.service.analytics.AdobeTracker
import com.worldventures.wallet.analytics.WalletAnalyticsAction

@AnalyticsEvent(action = "wallet:setup:Step 4:Set Display Photo and Name", navigationState = true, trackers = arrayOf(AdobeTracker.TRACKER_KEY))
class SetupUserAction : WalletAnalyticsAction() {

   @Attribute("cardsetupstep4") internal val cardSetupStep4 = "1"
}
