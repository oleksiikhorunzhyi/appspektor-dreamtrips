package com.worldventures.wallet.analytics

import com.worldventures.core.service.analytics.AdobeTracker
import com.worldventures.core.service.analytics.Attribute
import com.worldventures.janet.analytics.AnalyticsEvent
import com.worldventures.wallet.domain.entity.SmartCard
import com.worldventures.wallet.domain.entity.SmartCardFirmware
import com.worldventures.wallet.domain.entity.SmartCardStatus
import com.worldventures.wallet.domain.entity.record.Record

@AnalyticsEvent(action = "wallet:Add a Card:Card Detail", navigationState = true, trackers = arrayOf(AdobeTracker.TRACKER_KEY))
class AddCardDetailsAction(record: Record) : BaseCardDetailsAction() {

   @Attribute("addstate") internal lateinit var addState: String

   init {
      fillRecordDetails(record)
   }

   override fun setSmartCardAction(smartCard: SmartCard?, smartCardStatus: SmartCardStatus, smartCardFirmware: SmartCardFirmware?) {
      super.setSmartCardAction(smartCard, smartCardStatus, smartCardFirmware)
      addState = if (smartCardStatus.connectionStatus.isConnected) "Online" else "Offline"
   }
}
