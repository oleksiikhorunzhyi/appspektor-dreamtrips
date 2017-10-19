package com.worldventures.dreamtrips.wallet.analytics;

import com.worldventures.core.service.analytics.AnalyticsEvent;
import com.worldventures.core.service.analytics.AdobeTracker;
import com.worldventures.dreamtrips.wallet.domain.entity.record.Record;

@AnalyticsEvent(action = "wallet:Add Default Card:Set as Default Card",
                trackers = AdobeTracker.TRACKER_KEY)
public class SetDefaultCardAction extends BaseSetDefaultCardAction {

   private SetDefaultCardAction() {
   }

   public static SetDefaultCardAction forBankCard(Record record) {
      SetDefaultCardAction action = new SetDefaultCardAction();
      action.setDefaultWhere = "In-App:Wallet Home";
      action.fillRecordDetails(record);
      return action;
   }
}
