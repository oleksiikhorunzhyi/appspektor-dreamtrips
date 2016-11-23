package com.worldventures.dreamtrips.wallet.domain.entity.card;

import com.worldventures.dreamtrips.wallet.domain.entity.AddressInfo;
import com.worldventures.dreamtrips.wallet.domain.entity.ImmutableAddressInfo;
import com.worldventures.dreamtrips.wallet.domain.entity.ImmutableRecordIssuerInfo;
import com.worldventures.dreamtrips.wallet.domain.entity.RecordIssuerInfo;

import org.immutables.value.Value;
import org.jetbrains.annotations.Nullable;

@Value.Immutable
public abstract class BankCard extends Card {

   @Value.Default
   public String cardNameHolder() {
      return "";
   }

   @Value.Default
   public String nickName() {
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

   @Value.Default
   @Override
   public Category category() {
      return Category.BANK;
   }

   public enum CardType {
      DEBIT, CREDIT, UNKNOWN
   }
}
