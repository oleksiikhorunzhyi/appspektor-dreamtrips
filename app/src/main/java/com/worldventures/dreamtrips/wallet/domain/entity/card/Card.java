package com.worldventures.dreamtrips.wallet.domain.entity.card;

import android.support.annotation.Nullable;

public interface Card {

   @Nullable
   String id();

   long number();

   String expDate();

   Category category();

   enum Category {
      BANK, DISCOUNT, SAMPLE
   }
}
