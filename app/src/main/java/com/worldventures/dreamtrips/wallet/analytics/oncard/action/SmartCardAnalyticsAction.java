package com.worldventures.dreamtrips.wallet.analytics.oncard.action;

import android.support.annotation.CallSuper;

import com.worldventures.dreamtrips.core.utils.tracksystem.AttributeMap;
import com.worldventures.dreamtrips.wallet.analytics.BaseCardDetailsAction;
import com.worldventures.dreamtrips.wallet.util.TimeUtils;

import java.util.HashMap;
import java.util.Map;

import io.techery.janet.smartcard.model.analytics.AnalyticsLog;

import static io.techery.janet.smartcard.model.analytics.AnalyticsLog.BATTERY;
import static io.techery.janet.smartcard.model.analytics.AnalyticsLog.BATTERY_CRITICAL;
import static io.techery.janet.smartcard.model.analytics.AnalyticsLog.CARD_READ_ERROR;
import static io.techery.janet.smartcard.model.analytics.AnalyticsLog.CARD_READ_FORMAT_ERROR;
import static io.techery.janet.smartcard.model.analytics.AnalyticsLog.CARD_READ_NAME_ERROR;
import static io.techery.janet.smartcard.model.analytics.AnalyticsLog.CARD_READ_PASS;
import static io.techery.janet.smartcard.model.analytics.AnalyticsLog.CARD_SWIPE;
import static io.techery.janet.smartcard.model.analytics.AnalyticsLog.DEFAULT_CARD_WIPE;
import static io.techery.janet.smartcard.model.analytics.AnalyticsLog.ENTER_CHARGER;
import static io.techery.janet.smartcard.model.analytics.AnalyticsLog.ENTER_PIN_MODE;
import static io.techery.janet.smartcard.model.analytics.AnalyticsLog.EXCEPTION;
import static io.techery.janet.smartcard.model.analytics.AnalyticsLog.EXIT_CHARGER;
import static io.techery.janet.smartcard.model.analytics.AnalyticsLog.PAYMENT_CARD_WIPE;
import static io.techery.janet.smartcard.model.analytics.AnalyticsLog.PAYMENT_MODE;
import static io.techery.janet.smartcard.model.analytics.AnalyticsLog.PIN_FAIL;
import static io.techery.janet.smartcard.model.analytics.AnalyticsLog.PIN_LOCKOUT;
import static io.techery.janet.smartcard.model.analytics.AnalyticsLog.PIN_RESET;
import static io.techery.janet.smartcard.model.analytics.AnalyticsLog.PIN_UNLOCK;
import static io.techery.janet.smartcard.model.analytics.AnalyticsLog.POWER_OFF;
import static io.techery.janet.smartcard.model.analytics.AnalyticsLog.POWER_ON;
import static io.techery.janet.smartcard.model.analytics.AnalyticsLog.RESTART;
import static io.techery.janet.smartcard.model.analytics.AnalyticsLog.REWARDS_BEACON;
import static io.techery.janet.smartcard.model.analytics.AnalyticsLog.SET_TIME;
import static io.techery.janet.smartcard.model.analytics.AnalyticsLog.USER_ASSIGNED;
import static io.techery.janet.smartcard.model.analytics.AnalyticsLog.USER_UNASSIGNED;

public abstract class SmartCardAnalyticsAction extends BaseCardDetailsAction {

   public static SmartCardAnalyticsAction from(AnalyticsLog analyticsLog) {
      SmartCardAnalyticsAction analyticsAction = null;


      int type = analyticsLog.type();
      switch (type) {
         case EXCEPTION:
            analyticsAction = new SmartCardExceptionAction(analyticsLog);
            break;
         case BATTERY:
         case BATTERY_CRITICAL:
            analyticsAction = new SmartCardBatteryAction(analyticsLog);
            break;
         case PAYMENT_MODE:
         case CARD_SWIPE:
            analyticsAction = new SmartCardPaymentAction(analyticsLog);
            break;
         case CARD_READ_PASS:
         case CARD_READ_ERROR:
         case CARD_READ_FORMAT_ERROR:
         case CARD_READ_NAME_ERROR:
            analyticsAction = new SmartCardAddRecordAction(analyticsLog);
            break;
         case ENTER_PIN_MODE:
         case PIN_UNLOCK:
         case PIN_FAIL:
         case PIN_LOCKOUT:
         case PIN_RESET:
            analyticsAction = new SmartCardPinAction(analyticsLog);
            break;
         case POWER_ON:
         case POWER_OFF:
         case ENTER_CHARGER:
         case EXIT_CHARGER:
            analyticsAction = new SmartCardPowerAction(analyticsLog);
            break;
         case PAYMENT_CARD_WIPE:
         case DEFAULT_CARD_WIPE:
            analyticsAction = new SmartCardWipeAction(analyticsLog);
            break;
         case RESTART:
         case SET_TIME:
            analyticsAction = new SmartCardSystemAction(analyticsLog);
            break;
         case USER_ASSIGNED:
            analyticsAction = new SmartCardUserAssignAction(analyticsLog);
            break;
         case USER_UNASSIGNED:
            analyticsAction = new SmartCardUserUnassignAction(analyticsLog);
            break;
         case REWARDS_BEACON:
            analyticsAction = new SmartCardRewardModeAction(analyticsLog);
            break;
      }

      return analyticsAction;
   }

   @AttributeMap final Map<String, String> attributeMap = new HashMap<>();

   private final AnalyticsLog analyticsLog;

   SmartCardAnalyticsAction(AnalyticsLog analyticsLog) {
      this.analyticsLog = analyticsLog;
      processLog(analyticsLog.type(), analyticsLog);
   }

   @CallSuper
   protected void processLog(int type, AnalyticsLog logEntry) {
      attributeMap.put("octimepart", TimeUtils.formatToIso(logEntry.timestampMillis()));
   }

   @Override
   public String toString() {
      return "SmartCardAnalyticsAction :: type = " + analyticsLog.type() + ", attributes = " + attributeMap;
   }

}
