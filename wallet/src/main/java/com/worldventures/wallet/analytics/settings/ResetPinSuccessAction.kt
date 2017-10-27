package com.worldventures.wallet.analytics.settings

import com.worldventures.janet.analytics.AnalyticsEvent
import com.worldventures.core.service.analytics.Attribute
import com.worldventures.core.service.analytics.AdobeTracker
import com.worldventures.wallet.analytics.WalletAnalyticsAction

@AnalyticsEvent(action = "wallet:setup:settings:successfully reset your PIN", trackers = arrayOf(AdobeTracker.TRACKER_KEY))
class ResetPinSuccessAction : WalletAnalyticsAction() {

   @Attribute("resetpin2") internal var resetPin = "1"
}
