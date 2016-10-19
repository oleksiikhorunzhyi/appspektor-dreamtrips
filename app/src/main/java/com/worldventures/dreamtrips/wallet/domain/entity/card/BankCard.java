package com.worldventures.dreamtrips.wallet.domain.entity.card;

import com.worldventures.dreamtrips.wallet.domain.entity.AddressInfo;
import com.worldventures.dreamtrips.wallet.domain.entity.ImmutableAddressInfo;
import com.worldventures.dreamtrips.wallet.domain.entity.ImmutableRecordIssuerInfo;
import com.worldventures.dreamtrips.wallet.domain.entity.RecordIssuerInfo;

import org.immutables.value.Value;

@Value.Immutable
public abstract class BankCard implements Card {

   @Value.Default
   public String title() {
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
   public int cvv() {
      return 0;
   }

   @Value.Default
   @Override
   public Category category() {
      return Category.BANK;
   }

   public enum CardType {
      DEBIT, CREDIT, UNKNOWN
   }
}