package com.worldventures.dreamtrips.wallet.domain.entity.card;

import android.support.annotation.Nullable;

public abstract class Card {

   @Nullable
   public abstract String id();

   public abstract String number();

   public abstract String expDate();

   public abstract Category category();

   @Override
   public boolean equals(Object obj) {
      if (super.equals(obj)) return true;
      if (obj instanceof Card) {
         final Card card = (Card) obj;
         final String cardId = card.id();
         final String id = id();
         return (cardId != null && id != null && cardId.equals(id)) || card.number() != number();
      }
      return false;
   }

   public enum Category {
      BANK, DISCOUNT, SAMPLE
   }
}
