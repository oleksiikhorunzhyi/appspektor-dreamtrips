package com.worldventures.dreamtrips.wallet.analytics;

import com.worldventures.dreamtrips.core.utils.tracksystem.AdobeTracker;
import com.worldventures.dreamtrips.core.utils.tracksystem.AnalyticsEvent;
import com.worldventures.dreamtrips.core.utils.tracksystem.Attribute;
import com.worldventures.dreamtrips.wallet.domain.entity.record.Record;

@AnalyticsEvent(action = "wallet:Card Detail:Set as Default Card",
                trackers = AdobeTracker.TRACKER_KEY)
public class ChangeDefaultCardAction extends BaseSetDefaultCardAction {

   @Attribute("paycardnickname") String cardNickname;

   private ChangeDefaultCardAction() {
   }

   public static ChangeDefaultCardAction forBankCard(Record record) {
      ChangeDefaultCardAction action = new ChangeDefaultCardAction();
      action.setDefaultWhere = "In-App:Card Detail";
      action.fillPaycardInfo(record);
      action.cardNickname = record.nickName();
      return action;
   }
}
