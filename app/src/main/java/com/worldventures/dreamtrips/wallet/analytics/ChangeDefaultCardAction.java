package com.worldventures.dreamtrips.wallet.analytics;

import com.worldventures.core.service.analytics.AdobeTracker;
import com.worldventures.core.service.analytics.AnalyticsEvent;
import com.worldventures.core.service.analytics.Attribute;
import com.worldventures.dreamtrips.wallet.domain.entity.record.Record;

@AnalyticsEvent(action = "wallet:Card Detail:Set as Default Card",
                trackers = AdobeTracker.TRACKER_KEY)
public final class ChangeDefaultCardAction extends BaseSetDefaultCardAction {

   @Attribute("paycardnickname") String cardNickname;

   private ChangeDefaultCardAction() {
   }

   public static ChangeDefaultCardAction forBankCard(Record record) {
      ChangeDefaultCardAction action = new ChangeDefaultCardAction();
      action.setDefaultWhere = "In-App:Card Detail";
      action.fillRecordDetails(record);
      action.cardNickname = record.nickName();
      return action;
   }
}
