package com.worldventures.wallet.analytics.oncard.action;

import com.worldventures.core.service.analytics.AdobeTracker;
import com.worldventures.janet.analytics.AnalyticsEvent;

import io.techery.janet.smartcard.model.analytics.AnalyticsLog;

@AnalyticsEvent(action = "wallet:oncard:wipe", trackers = AdobeTracker.TRACKER_KEY)
class SmartCardWipeAction extends SmartCardAnalyticsAction {

   SmartCardWipeAction(AnalyticsLog logEntry) {
      super(logEntry);
   }

   @Override
   protected void processLog(int type, AnalyticsLog logEntry) {
      super.processLog(type, logEntry);
      switch (type) {
         case AnalyticsLog.PAYMENT_CARD_WIPE:
            attributeMap.put("ocwipepaycards", "1");
            break;
         case AnalyticsLog.DEFAULT_CARD_WIPE:
            attributeMap.put("ocwipedefaultcard", "1");
            break;
         default:
            break;
      }
   }
}
