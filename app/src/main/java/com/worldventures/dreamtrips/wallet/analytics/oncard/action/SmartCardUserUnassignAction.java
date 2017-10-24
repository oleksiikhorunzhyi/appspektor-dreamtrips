package com.worldventures.dreamtrips.wallet.analytics.oncard.action;

import com.worldventures.dreamtrips.core.utils.tracksystem.AdobeTracker;
import com.worldventures.dreamtrips.core.utils.tracksystem.AnalyticsEvent;

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