package com.worldventures.wallet.analytics.oncard.action;

import com.worldventures.core.service.analytics.AdobeTracker;
import com.worldventures.janet.analytics.AnalyticsEvent;

import io.techery.janet.smartcard.model.analytics.AnalyticsLog;

@AnalyticsEvent(action = "wallet:oncard:rewardmode", trackers = AdobeTracker.TRACKER_KEY)
class SmartCardRewardModeAction extends SmartCardAnalyticsAction {

   SmartCardRewardModeAction(AnalyticsLog logEntry) {
      super(logEntry);
   }

   @Override
   protected void processLog(int type, AnalyticsLog logEntry) {
      super.processLog(type, logEntry);
      if (type == AnalyticsLog.REWARDS_BEACON) {
         attributeMap.put("ocrewardmode", "1");
      }
   }
}
