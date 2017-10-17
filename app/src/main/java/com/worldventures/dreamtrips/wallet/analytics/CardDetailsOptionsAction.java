package com.worldventures.dreamtrips.wallet.analytics;

import com.worldventures.core.service.analytics.AdobeTracker;
import com.worldventures.core.service.analytics.AnalyticsEvent;
import com.worldventures.core.service.analytics.Attribute;
import com.worldventures.dreamtrips.wallet.domain.entity.record.Record;

@AnalyticsEvent(action = "wallet:Add a Card:Card Detail Options",
                trackers = AdobeTracker.TRACKER_KEY)
public class CardDetailsOptionsAction extends BaseCardDetailsWithDefaultAction {

   @Attribute("setdefaultwhere") final String setDefaultWhere = "In-App:Setup";
   @Attribute("changedefaultaddress") final String changeDefaultAddress = "1";
   @Attribute("paycardnickname") final String nickname;

   public CardDetailsOptionsAction(String nickname) {
      this.nickname = nickname;
   }

   public static CardDetailsOptionsAction forBankCard(Record record, boolean isDefault) {
      CardDetailsOptionsAction action = new CardDetailsOptionsAction(record.nickName());
      action.fillPaycardInfo(record, isDefault);
      return action;
   }
}
