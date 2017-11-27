package com.worldventures.wallet.analytics.locatecard.action

import com.worldventures.janet.analytics.AnalyticsEvent
import com.worldventures.core.service.analytics.Attribute
import com.worldventures.core.service.analytics.AdobeTracker
import com.worldventures.wallet.domain.entity.lostcard.WalletCoordinates

@AnalyticsEvent(action = "wallet:settings:locate smartcard:display location", navigationState = true, trackers = arrayOf(AdobeTracker.TRACKER_KEY))
class DisplayMapAnalyticsAction : BaseLocateSmartCardAction() {

   @Attribute("trackingenabled") internal val trackingEnabled = "Yes"
   @Attribute("locationavailable") internal var locationAvailable = "No"

   override fun setLocation(walletCoordinates: WalletCoordinates) {
      super.setLocation(walletCoordinates)
      locationAvailable = "Yes"
   }
}
