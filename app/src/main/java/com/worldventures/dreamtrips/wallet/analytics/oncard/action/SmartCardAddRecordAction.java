package com.worldventures.dreamtrips.wallet.analytics.oncard.action;

import com.worldventures.dreamtrips.core.utils.tracksystem.AdobeTracker;
import com.worldventures.dreamtrips.core.utils.tracksystem.AnalyticsEvent;

import io.techery.janet.smartcard.model.analytics.AnalyticsLog;

@AnalyticsEvent(action = "wallet:oncard:card add", trackers = AdobeTracker.TRACKER_KEY)
class SmartCardAddRecordAction extends SmartCardAnalyticsAction {

   SmartCardAddRecordAction(AnalyticsLog logEntry) {
      super(logEntry);
   }

   @Override
   protected void processLog(int type, AnalyticsLog logEntry) {
      super.processLog(type, logEntry);
      switch (type) {
         case AnalyticsLog.CARD_READ_PASS:
            attributeMap.put("ocsuccardswipe", "1");
            break;
         case AnalyticsLog.CARD_READ_ERROR:
            attributeMap.put("ocfailcardswipe", "1");
            break;
         case AnalyticsLog.CARD_READ_FORMAT_ERROR:
            attributeMap.put("ocformaterrorswipe", "1");
            break;
         case AnalyticsLog.CARD_READ_NAME_ERROR:
            attributeMap.put("ocnameerrorswipe", "1");
            break;
      }
   }
}