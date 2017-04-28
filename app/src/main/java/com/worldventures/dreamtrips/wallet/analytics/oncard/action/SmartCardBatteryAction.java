package com.worldventures.dreamtrips.wallet.analytics.oncard.action;

import com.worldventures.dreamtrips.core.utils.tracksystem.AdobeTracker;
import com.worldventures.dreamtrips.core.utils.tracksystem.AnalyticsEvent;

import io.techery.janet.smartcard.model.analytics.AnalyticsLog;
import io.techery.janet.smartcard.model.analytics.AnalyticsLogEntryBattery;

@AnalyticsEvent(action = "wallet:oncard:battery", trackers = AdobeTracker.TRACKER_KEY)
class SmartCardBatteryAction extends SmartCardAnalyticsAction {

   SmartCardBatteryAction(AnalyticsLog logEntry) {
      super(logEntry);
   }

   @Override
   protected void processLog(int type, AnalyticsLog logEntry) {
      super.processLog(type, logEntry);
      switch (type) {
         case AnalyticsLog.BATTERY:
            AnalyticsLogEntryBattery battery = (AnalyticsLogEntryBattery) logEntry;
            attributeMap.put("ocbat", String.valueOf(battery.batteryLevel()));
            break;
         case AnalyticsLog.BATTERY_CRITICAL:
            attributeMap.put("ocbatcritical", "1");
            break;
      }
   }
}