package com.worldventures.dreamtrips.wallet.domain.entity.card;

public interface Card {

   String NO_ID = "0";

   String id();

   long number();

   int expiryMonth();

   int expiryYear();

   Category category();

   enum Category {
      BANK, DISCOUNT, SAMPLE
   }
}