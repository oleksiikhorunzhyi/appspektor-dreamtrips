package com.worldventures.wallet.analytics.wizard

import com.worldventures.janet.analytics.AnalyticsEvent
import com.worldventures.core.service.analytics.Attribute
import com.worldventures.core.service.analytics.AdobeTracker
import com.worldventures.wallet.analytics.WalletAnalyticsAction

@AnalyticsEvent(action = "wallet:setup:Terms and Conditions:Agree", trackers = arrayOf(AdobeTracker.TRACKER_KEY))
class TermsAcceptedAction : WalletAnalyticsAction() {
   @Attribute("tocagree") internal val tocAgree = "1"
}
