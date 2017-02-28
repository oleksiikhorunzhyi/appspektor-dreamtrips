package com.worldventures.dreamtrips.wallet.domain.entity.card;

import com.worldventures.dreamtrips.wallet.domain.entity.AddressInfo;
import com.worldventures.dreamtrips.wallet.domain.entity.ImmutableAddressInfo;
import com.worldventures.dreamtrips.wallet.domain.entity.ImmutableRecordIssuerInfo;
import com.worldventures.dreamtrips.wallet.domain.entity.RecordIssuerInfo;

import org.immutables.gson.Gson;
import org.immutables.value.Value;
import org.jetbrains.annotations.Nullable;

@Value.Immutable
@Gson.TypeAdapters
public abstract class BankCard extends Card {

   @Nullable
   public abstract String numberLastFourDigits();

   @Value.Default
   public String cardNameHolder() {
      return "";
   }

   @Value.Default
   public String cardHolderFirstName() {
      return "";
   }

   @Value.Default
   public String cardHolderMiddleName() {
      return "";
   }

   @Value.Default
   public String cardHolderLastName() {
      return "";
   }


   @Value.Default
   public String nickName() { // TODO: 12/6/16 remove to cardName
      return "";
   }

   @Value.Default
   public AddressInfo addressInfo() {
      return ImmutableAddressInfo.builder().build();
   }

   @Value.Default
   public RecordIssuerInfo issuerInfo() {
      return ImmutableRecordIssuerInfo.builder().build();
   }

   @Value.Default
   public String cvv() {
      return "";
   }

   @Nullable
   public abstract String track1();

   @Nullable
   public abstract String track2();

   @Nullable
   public abstract String track3();

   @Value.Default
   @Override
   public Category category() {
      return Category.BANK;
   }

   public enum CardType {
      DEBIT, CREDIT, UNKNOWN
   }
}
