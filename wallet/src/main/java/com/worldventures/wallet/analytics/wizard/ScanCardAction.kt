package com.worldventures.wallet.analytics.wizard

import com.worldventures.janet.analytics.AnalyticsEvent
import com.worldventures.core.service.analytics.Attribute
import com.worldventures.core.service.analytics.AdobeTracker
import com.worldventures.wallet.analytics.WalletAnalyticsAction

@AnalyticsEvent(action = "wallet:setup:Step 1:Scan Card", navigationState = true, trackers = arrayOf(AdobeTracker.TRACKER_KEY))
class ScanCardAction : WalletAnalyticsAction() {

   @Attribute("cardsetupstart") internal val cardSetupStart = "1"
}
