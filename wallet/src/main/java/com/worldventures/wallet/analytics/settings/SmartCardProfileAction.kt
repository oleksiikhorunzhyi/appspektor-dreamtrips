package com.worldventures.wallet.analytics.settings

import com.worldventures.janet.analytics.AnalyticsEvent
import com.worldventures.core.service.analytics.AdobeTracker
import com.worldventures.wallet.analytics.WalletAnalyticsAction

@AnalyticsEvent(action = "wallet:settings:general:smartcard profile", navigationState = true, trackers = arrayOf(AdobeTracker.TRACKER_KEY))
class SmartCardProfileAction : WalletAnalyticsAction()
