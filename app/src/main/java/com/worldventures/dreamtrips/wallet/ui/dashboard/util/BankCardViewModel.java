package com.worldventures.dreamtrips.wallet.ui.dashboard.util;

import com.worldventures.dreamtrips.wallet.domain.entity.record.Record;

public class BankCardViewModel {

   public final Record record;
   public final boolean defaultCard;

   public BankCardViewModel(Record record, boolean defaultCard) {
      this.record = record;
      this.defaultCard = defaultCard;
   }

}
