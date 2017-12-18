package com.worldventures.wallet.analytics.oncard.action

import com.worldventures.janet.analytics.AnalyticsEvent
import com.worldventures.core.service.analytics.AdobeTracker

import io.techery.janet.smartcard.model.analytics.AnalyticsLog
import io.techery.janet.smartcard.model.analytics.AnalyticsLogEntryBattery

@AnalyticsEvent(action = "wallet:oncard:battery", trackers = arrayOf(AdobeTracker.TRACKER_KEY))
internal class SmartCardBatteryAction(logEntry: AnalyticsLog) : SmartCardAnalyticsAction(logEntry) {

   @Suppress("UnsafeCast")
   override fun processLog(type: Int, logEntry: AnalyticsLog) {
      super.processLog(type, logEntry)
      when (type) {
         AnalyticsLog.BATTERY -> {
            val battery = logEntry as AnalyticsLogEntryBattery
            attributeMap.put("ocbat", battery.batteryLevel().toString())
         }
         AnalyticsLog.BATTERY_CRITICAL -> attributeMap.put("ocbatcritical", "1")
      }
   }
}
