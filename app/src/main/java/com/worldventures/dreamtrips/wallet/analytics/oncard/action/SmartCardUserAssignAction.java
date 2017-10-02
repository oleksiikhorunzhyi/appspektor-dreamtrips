package com.worldventures.dreamtrips.wallet.analytics.oncard.action;

import com.worldventures.core.service.analytics.AnalyticsEvent;
import com.worldventures.core.service.analytics.AdobeTracker;

import io.techery.janet.smartcard.model.analytics.AnalyticsLog;

@AnalyticsEvent(action = "wallet:oncard:user assigned", trackers = AdobeTracker.TRACKER_KEY)
class SmartCardUserAssignAction extends SmartCardUserAction {

   SmartCardUserAssignAction(AnalyticsLog logEntry) {
      super(logEntry);
   }

   @Override
   public void setUserId(int userId) {
      if (userId != 0) attributeMap.put("ocmemassigned", String.valueOf(userId));
   }
}