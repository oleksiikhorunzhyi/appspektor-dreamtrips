package com.worldventures.dreamtrips.wallet.analytics;

import com.worldventures.dreamtrips.core.utils.tracksystem.AdobeTracker;
import com.worldventures.dreamtrips.core.utils.tracksystem.AnalyticsEvent;
import com.worldventures.dreamtrips.core.utils.tracksystem.Attribute;
import com.worldventures.dreamtrips.wallet.domain.entity.card.BankCard;

@AnalyticsEvent(action = "wallet:Add a Card:Card Detail Options",
                trackers = AdobeTracker.TRACKER_KEY)
public class CardDetailsOptionsAction extends BaseCardDetailsWithDefaultAction {

   @Attribute("setdefaultwhere") final String setDefaultWhere = "In-App:Setup";
   @Attribute("changedefaultaddress") final String changeDefaultAddress = "1";
   @Attribute("paycardnickname") final String nickname;

   public CardDetailsOptionsAction(String nickname) {
      this.nickname = nickname;
   }

   public static CardDetailsOptionsAction forBankCard(BankCard bankCard, boolean isDefault) {
      CardDetailsOptionsAction action = new CardDetailsOptionsAction(bankCard.nickName());
      action.fillPaycardInfo(bankCard, isDefault);
      return action;
   }
}
