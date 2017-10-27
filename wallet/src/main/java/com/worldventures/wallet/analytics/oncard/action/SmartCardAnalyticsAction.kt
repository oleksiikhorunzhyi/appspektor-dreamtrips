package com.worldventures.wallet.analytics.oncard.action

import android.support.annotation.CallSuper
import com.worldventures.wallet.analytics.BaseCardDetailsAction
import com.worldventures.wallet.util.TimeUtils
import io.techery.janet.smartcard.model.analytics.AnalyticsLog
import io.techery.janet.smartcard.model.analytics.AnalyticsLog.*

abstract class SmartCardAnalyticsAction internal constructor(private val analyticsLog: AnalyticsLog) : BaseCardDetailsAction() {

   init {
      processLog(analyticsLog.type(), analyticsLog)
   }

   @CallSuper
   protected open fun processLog(type: Int, logEntry: AnalyticsLog) {
      attributeMap.put("octimepart", TimeUtils.formatToIso(logEntry.timestampMillis()))
   }

   override fun toString(): String {
      return "SmartCardAnalyticsAction :: type = " + analyticsLog.type() + ", attributes = " + attributeMap
   }

   companion object {

      fun from(analyticsLog: AnalyticsLog): SmartCardAnalyticsAction? {
         var analyticsAction: SmartCardAnalyticsAction? = null


         val type = analyticsLog.type()
         when (type) {
            EXCEPTION -> analyticsAction = SmartCardExceptionAction(analyticsLog)
            BATTERY, BATTERY_CRITICAL -> analyticsAction = SmartCardBatteryAction(analyticsLog)
            PAYMENT_MODE, CARD_SWIPE -> analyticsAction = SmartCardPaymentAction(analyticsLog)
            CARD_READ_PASS, CARD_READ_ERROR, CARD_READ_FORMAT_ERROR, CARD_READ_NAME_ERROR -> analyticsAction = SmartCardAddRecordAction(analyticsLog)
            ENTER_PIN_MODE, PIN_UNLOCK, PIN_FAIL, PIN_LOCKOUT, PIN_RESET -> analyticsAction = SmartCardPinAction(analyticsLog)
            POWER_ON, POWER_OFF, ENTER_CHARGER, EXIT_CHARGER -> analyticsAction = SmartCardPowerAction(analyticsLog)
            PAYMENT_CARD_WIPE, DEFAULT_CARD_WIPE -> analyticsAction = SmartCardWipeAction(analyticsLog)
            RESTART, SET_TIME -> analyticsAction = SmartCardSystemAction(analyticsLog)
            USER_ASSIGNED -> analyticsAction = SmartCardUserAssignAction(analyticsLog)
            USER_UNASSIGNED -> analyticsAction = SmartCardUserUnassignAction(analyticsLog)
            REWARDS_BEACON -> analyticsAction = SmartCardRewardModeAction(analyticsLog)
         }

         return analyticsAction
      }
   }

}
