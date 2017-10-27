package com.worldventures.wallet.analytics.oncard.action

import io.techery.janet.smartcard.model.analytics.AnalyticsLog

abstract class SmartCardUserAction internal constructor(logEntry: AnalyticsLog) : SmartCardAnalyticsAction(logEntry) {

   abstract fun setUserId(userId: Int)

   override fun processLog(type: Int, logEntry: AnalyticsLog) {
      super.processLog(type, logEntry)
      when (type) {
         AnalyticsLog.USER_ASSIGNED -> attributeMap.put("ocassigned", "1")
         AnalyticsLog.USER_UNASSIGNED -> attributeMap.put("ocunassigned ", "1")
      }
   }

}