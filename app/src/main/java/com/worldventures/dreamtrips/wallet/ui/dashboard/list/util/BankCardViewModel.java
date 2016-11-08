package com.worldventures.dreamtrips.wallet.ui.dashboard.list.util;

import com.worldventures.dreamtrips.wallet.domain.entity.card.BankCard;

public class BankCardViewModel {

   public final BankCard bankCard;
   public final boolean defaultCard;

   public BankCardViewModel(BankCard bankCard, boolean defaultCard) {
      this.bankCard = bankCard;
      this.defaultCard = defaultCard;
   }

}
