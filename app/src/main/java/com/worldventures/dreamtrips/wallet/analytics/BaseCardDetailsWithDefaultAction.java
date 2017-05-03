package com.worldventures.dreamtrips.wallet.analytics;

import com.worldventures.dreamtrips.core.utils.tracksystem.Attribute;
import com.worldventures.dreamtrips.wallet.domain.entity.card.BankCard;

public abstract class BaseCardDetailsWithDefaultAction extends BaseCardDetailsAction {

   @Attribute("defaultpaycard") String defaultPaycard;

   public void fillPaycardInfo(BankCard bankCard, boolean isDefault) {
      fillPaycardInfo(bankCard);
      defaultPaycard = isDefault ? "Yes" : "No";
   }
}
