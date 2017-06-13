package com.worldventures.dreamtrips.wallet.analytics.oncard.action;

import com.worldventures.dreamtrips.core.utils.tracksystem.AdobeTracker;
import com.worldventures.dreamtrips.core.utils.tracksystem.AnalyticsEvent;

import io.techery.janet.smartcard.model.analytics.AnalyticsLog;

@AnalyticsEvent(action = "wallet:oncard:power", trackers = AdobeTracker.TRACKER_KEY)
class SmartCardPowerAction extends SmartCardAnalyticsAction {

   SmartCardPowerAction(AnalyticsLog logEntry) {
      super(logEntry);
   }

   @Override
   protected void processLog(int type, AnalyticsLog logEntry) {
      super.processLog(type, logEntry);
      switch (type) {
         case AnalyticsLog.POWER_ON:
            attributeMap.put("ocpoweron", "1");
            break;
         case AnalyticsLog.POWER_OFF:
            attributeMap.put("ocpoweroff", "1");
            break;
         case AnalyticsLog.ENTER_CHARGER:
            attributeMap.put("ocentercharger", "1");
            break;
         case AnalyticsLog.EXIT_CHARGER:
            attributeMap.put("ocexitcharger", "1");
            break;
      }
   }
}