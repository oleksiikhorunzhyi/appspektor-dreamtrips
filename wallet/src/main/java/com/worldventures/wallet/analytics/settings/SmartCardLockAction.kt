package com.worldventures.wallet.analytics.settings

import com.worldventures.janet.analytics.AnalyticsEvent
import com.worldventures.core.service.analytics.Attribute
import com.worldventures.core.service.analytics.AdobeTracker
import com.worldventures.wallet.analytics.WalletAnalyticsAction

@AnalyticsEvent(action = "wallet:settings:lock card", trackers = arrayOf(AdobeTracker.TRACKER_KEY))
class SmartCardLockAction : WalletAnalyticsAction() {

   @Attribute("cardlock") internal var cardlock = "1"
}
