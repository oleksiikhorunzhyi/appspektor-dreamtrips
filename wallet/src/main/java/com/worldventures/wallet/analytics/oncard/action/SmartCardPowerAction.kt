package com.worldventures.wallet.analytics.oncard.action

import com.worldventures.janet.analytics.AnalyticsEvent
import com.worldventures.core.service.analytics.AdobeTracker

import io.techery.janet.smartcard.model.analytics.AnalyticsLog

@AnalyticsEvent(action = "wallet:oncard:power", trackers = arrayOf(AdobeTracker.TRACKER_KEY))
internal class SmartCardPowerAction(logEntry: AnalyticsLog) : SmartCardAnalyticsAction(logEntry) {

   override fun processLog(type: Int, logEntry: AnalyticsLog) {
      super.processLog(type, logEntry)
      when (type) {
         AnalyticsLog.POWER_ON -> attributeMap.put("ocpoweron", "1")
         AnalyticsLog.POWER_OFF -> attributeMap.put("ocpoweroff", "1")
         AnalyticsLog.ENTER_CHARGER -> attributeMap.put("ocentercharger", "1")
         AnalyticsLog.EXIT_CHARGER -> attributeMap.put("ocexitcharger", "1")
      }
   }
}
