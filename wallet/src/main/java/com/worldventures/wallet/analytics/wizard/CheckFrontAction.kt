package com.worldventures.wallet.analytics.wizard

import com.worldventures.janet.analytics.AnalyticsEvent
import com.worldventures.core.service.analytics.AdobeTracker
import com.worldventures.wallet.analytics.WalletAnalyticsAction

@AnalyticsEvent(action = "wallet:setup:Check Front of SmartCard", navigationState = true, trackers = arrayOf(AdobeTracker.TRACKER_KEY))
class CheckFrontAction : WalletAnalyticsAction()
