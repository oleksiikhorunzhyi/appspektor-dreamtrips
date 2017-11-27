package com.worldventures.wallet.analytics.settings

import com.worldventures.janet.analytics.AnalyticsEvent
import com.worldventures.core.service.analytics.Attribute
import com.worldventures.core.service.analytics.AdobeTracker
import com.worldventures.wallet.analytics.WalletAnalyticsAction

@AnalyticsEvent(action = "wallet:settings:reset your PIN", navigationState = true, trackers = arrayOf(AdobeTracker.TRACKER_KEY))
class ResetPinAction : WalletAnalyticsAction() {

   @Attribute("resetpin1") internal var resetPin = "1"
}
