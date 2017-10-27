package com.worldventures.wallet.analytics.oncard.action

import com.worldventures.janet.analytics.AnalyticsEvent
import com.worldventures.core.service.analytics.AdobeTracker

import io.techery.janet.smartcard.model.analytics.AnalyticsLog

@AnalyticsEvent(action = "wallet:oncard:pin", trackers = arrayOf(AdobeTracker.TRACKER_KEY))
internal class SmartCardPinAction(logEntry: AnalyticsLog) : SmartCardAnalyticsAction(logEntry) {

   override fun processLog(type: Int, logEntry: AnalyticsLog) {
      super.processLog(type, logEntry)
      when (type) {
         AnalyticsLog.ENTER_PIN_MODE -> attributeMap.put("ocenterpinm", "1")
         AnalyticsLog.PIN_UNLOCK -> attributeMap.put("ocpinunlock", "1")
         AnalyticsLog.PIN_FAIL -> attributeMap.put("ocpinfail", "1")
         AnalyticsLog.PIN_LOCKOUT -> attributeMap.put("ocpinlockout", "1")
         AnalyticsLog.PIN_RESET -> attributeMap.put("ocpinreset", "1")
      }
   }
}