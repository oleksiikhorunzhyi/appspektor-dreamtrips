package com.worldventures.wallet.analytics.wizard

import com.worldventures.janet.analytics.AnalyticsEvent
import com.worldventures.core.service.analytics.Attribute
import com.worldventures.core.service.analytics.AdobeTracker
import com.worldventures.wallet.analytics.WalletAnalyticsAction

@AnalyticsEvent(action = "wallet:setup:Step 8:Setup Complete", navigationState = true, trackers = arrayOf(AdobeTracker.TRACKER_KEY))
class SetupCompleteAction : WalletAnalyticsAction() {

   @Attribute("cardsetupstep8") internal val cardSetupStep8 = "1"
}
