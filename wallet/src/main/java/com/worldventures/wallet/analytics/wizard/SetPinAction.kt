package com.worldventures.wallet.analytics.wizard

import com.worldventures.janet.analytics.AnalyticsEvent
import com.worldventures.core.service.analytics.Attribute
import com.worldventures.core.service.analytics.AdobeTracker
import com.worldventures.wallet.analytics.WalletAnalyticsAction

@AnalyticsEvent(action = "wallet:setup:Step 6:Create your PIN", navigationState = true, trackers = arrayOf(AdobeTracker.TRACKER_KEY))
class SetPinAction : WalletAnalyticsAction() {

   @Attribute("cardsetupstep6") internal val cardSetupStep6 = "1"
}
