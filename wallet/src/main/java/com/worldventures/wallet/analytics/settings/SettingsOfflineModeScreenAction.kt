package com.worldventures.wallet.analytics.settings

import com.worldventures.janet.analytics.AnalyticsEvent
import com.worldventures.core.service.analytics.Attribute
import com.worldventures.core.service.analytics.AdobeTracker
import com.worldventures.wallet.analytics.WalletAnalyticsAction

@AnalyticsEvent(action = "wallet:settings:security:offline mode", navigationState = true, trackers = arrayOf(AdobeTracker.TRACKER_KEY))
class SettingsOfflineModeScreenAction(offlineModeState: Boolean) : WalletAnalyticsAction() {

   @Attribute("offlinemode") internal val offlineMode: String = if (offlineModeState) "Enabled" else "Disabled"

}
