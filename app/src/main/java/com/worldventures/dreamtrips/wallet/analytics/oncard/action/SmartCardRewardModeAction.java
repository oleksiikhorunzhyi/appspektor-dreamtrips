package com.worldventures.dreamtrips.wallet.analytics.oncard.action;

import com.worldventures.dreamtrips.core.utils.tracksystem.AdobeTracker;
import com.worldventures.dreamtrips.core.utils.tracksystem.AnalyticsEvent;

import io.techery.janet.smartcard.model.analytics.AnalyticsLog;

@AnalyticsEvent(action = "wallet:oncard:rewardmode", trackers = AdobeTracker.TRACKER_KEY)
class SmartCardRewardModeAction extends SmartCardAnalyticsAction {

   SmartCardRewardModeAction(AnalyticsLog logEntry) {
      super(logEntry);
   }

   @Override
   protected void processLog(int type, AnalyticsLog logEntry) {
      super.processLog(type, logEntry);
      switch (type) {
         case AnalyticsLog.REWARDS_BEACON:
            attributeMap.put("ocrewardmode", "1");
            break;
      }
   }
}