package com.worldventures.wallet.analytics.new_smartcard

import com.worldventures.janet.analytics.AnalyticsEvent
import com.worldventures.core.service.analytics.Attribute
import com.worldventures.core.service.analytics.AdobeTracker
import com.worldventures.wallet.analytics.WalletAnalyticsAction

@AnalyticsEvent(action = "wallet:setup:Step 8:Syncing of Payment Cards",
      navigationState = true, trackers = arrayOf(AdobeTracker.TRACKER_KEY))
class SyncPaymentCardAction : WalletAnalyticsAction() {

   @Attribute("cardsetupstep8a") internal val cardSetupStep8a = "1"
}