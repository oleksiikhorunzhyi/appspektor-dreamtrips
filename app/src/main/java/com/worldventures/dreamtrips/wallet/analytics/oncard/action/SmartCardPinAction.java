package com.worldventures.dreamtrips.wallet.analytics.oncard.action;

import com.worldventures.dreamtrips.core.utils.tracksystem.AdobeTracker;
import com.worldventures.dreamtrips.core.utils.tracksystem.AnalyticsEvent;

import io.techery.janet.smartcard.model.analytics.AnalyticsLog;

@AnalyticsEvent(action = "wallet:oncard:pin", trackers = AdobeTracker.TRACKER_KEY)
class SmartCardPinAction extends SmartCardAnalyticsAction {

   SmartCardPinAction(AnalyticsLog logEntry) {
      super(logEntry);
   }

   @Override
   protected void processLog(int type, AnalyticsLog logEntry) {
      super.processLog(type, logEntry);
      switch (type) {
         case AnalyticsLog.ENTER_PIN_MODE:
            attributeMap.put("ocenterpinm", "1");
            break;
         case AnalyticsLog.PIN_UNLOCK:
            attributeMap.put("ocpinunlock", "1");
            break;
         case AnalyticsLog.PIN_FAIL:
            attributeMap.put("ocpinfail", "1");
            break;
         case AnalyticsLog.PIN_LOCKOUT:
            attributeMap.put("ocpinlockout", "1");
            break;
         case AnalyticsLog.PIN_RESET:
            attributeMap.put("ocpinreset", "1");
            break;
      }
   }
}