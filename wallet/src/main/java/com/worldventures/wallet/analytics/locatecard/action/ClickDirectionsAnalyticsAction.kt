package com.worldventures.wallet.analytics.locatecard.action

import com.worldventures.janet.analytics.AnalyticsEvent
import com.worldventures.core.service.analytics.Attribute
import com.worldventures.core.service.analytics.AdobeTracker
import com.worldventures.wallet.domain.entity.lostcard.WalletCoordinates

@AnalyticsEvent(action = "wallet:settings:locate smartcard:display location:get directions", trackers = arrayOf(AdobeTracker.TRACKER_KEY))
class ClickDirectionsAnalyticsAction : BaseLocateSmartCardAction() {

   @Attribute("getdirections") internal var getDirections = "1"
   @Attribute("trackingenabled") internal var trackingEnabled = "Yes"
   @Attribute("locationavailable") internal var locationAvailable = "No"

   override fun setLocation(walletCoordinates: WalletCoordinates) {
      super.setLocation(walletCoordinates)
      locationAvailable = "Yes"
   }
}
