package com.worldventures.dreamtrips.wallet.domain.entity.card;

import android.support.annotation.Nullable;

import com.worldventures.dreamtrips.wallet.domain.entity.AddressInfo;
import com.worldventures.dreamtrips.wallet.domain.entity.ImmutableRecordIssuerInfo;
import com.worldventures.dreamtrips.wallet.domain.entity.RecordIssuerInfo;

import org.immutables.value.Value;
import org.jetbrains.annotations.NotNull;

@Value.Immutable
public abstract class BankCard implements Card {

   @Nullable
   public abstract String title();

   @Nullable
   public abstract AddressInfo addressInfo();

   @Value.Default
   @NotNull
   public RecordIssuerInfo issuerInfo(){
      return ImmutableRecordIssuerInfo.builder().build();
   }

   @Value.Default
   public int cvv() {
      return 0;
   }

   public enum CardType {
      DEBIT, CREDIT
   }
}