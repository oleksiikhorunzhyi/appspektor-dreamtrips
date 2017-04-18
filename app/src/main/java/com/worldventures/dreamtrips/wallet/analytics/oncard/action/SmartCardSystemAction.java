package com.worldventures.dreamtrips.wallet.analytics.oncard.action;

import com.worldventures.dreamtrips.core.utils.tracksystem.AdobeTracker;
import com.worldventures.dreamtrips.core.utils.tracksystem.AnalyticsEvent;

import io.techery.janet.smartcard.model.analytics.AnalyticsLog;
import io.techery.janet.smartcard.model.analytics.AnalyticsLogEntryRestart;

@AnalyticsEvent(action = "wallet:oncard:system", trackers = AdobeTracker.TRACKER_KEY)
class SmartCardSystemAction extends SmartCardAnalyticsAction {

   SmartCardSystemAction(AnalyticsLog logEntry) {
      super(logEntry);
   }

   @Override
   protected void processLog(int type, AnalyticsLog logEntry) {
      super.processLog(type, logEntry);
      switch (type) {
         case AnalyticsLog.RESTART:
            AnalyticsLogEntryRestart restart = (AnalyticsLogEntryRestart) logEntry;
            attributeMap.put("ocrestart", "1");
            attributeMap.put("ocrestartcode", String.valueOf(restart.reason()));
            break;
         case AnalyticsLog.SET_TIME:
            attributeMap.put("ocsettime", "1");
            break;
      }
   }

}