package com.worldventures.wallet.analytics.oncard.action;

import com.worldventures.janet.analytics.AnalyticsEvent;
import com.worldventures.core.service.analytics.AdobeTracker;

import io.techery.janet.smartcard.model.analytics.AnalyticsLog;

@AnalyticsEvent(action = "wallet:oncard:user unassigned", trackers = AdobeTracker.TRACKER_KEY)
class SmartCardUserUnassignAction extends SmartCardUserAction {

   SmartCardUserUnassignAction(AnalyticsLog logEntry) {
      super(logEntry);
   }

   @Override
   public void setUserId(int userId) {
      if (userId != 0) attributeMap.put("ocmemunassigned", String.valueOf(userId));
   }
}