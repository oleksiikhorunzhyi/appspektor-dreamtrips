package com.worldventures.wallet.analytics.settings

import com.worldventures.janet.analytics.AnalyticsEvent
import com.worldventures.core.service.analytics.Attribute
import com.worldventures.core.service.analytics.AdobeTracker
import com.worldventures.wallet.analytics.WalletAnalyticsAction

@AnalyticsEvent(action = "wallet:settings:general:factory reset smart card", navigationState = true, trackers = arrayOf(AdobeTracker.TRACKER_KEY))
class FactoryResetAction : WalletAnalyticsAction() {

   @Attribute("factoryresetcard") internal var factoryResetCard = "1"
}
