package com.worldventures.wallet.analytics.new_smartcard

import com.worldventures.janet.analytics.AnalyticsEvent
import com.worldventures.core.service.analytics.Attribute
import com.worldventures.core.service.analytics.AdobeTracker
import com.worldventures.wallet.analytics.WalletAnalyticsAction

@AnalyticsEvent(action = "wallet:setup:Step 9:Setup of New Card Complete",
      navigationState = true, trackers = arrayOf(AdobeTracker.TRACKER_KEY))
class NewCardSetupCompleteAction : WalletAnalyticsAction() {

   @Attribute("cardsetupstep9") internal val cardSetupStep9 = "1"
}