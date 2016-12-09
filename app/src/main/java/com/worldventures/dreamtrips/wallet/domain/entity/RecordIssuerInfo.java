package com.worldventures.dreamtrips.wallet.domain.entity;


import com.worldventures.dreamtrips.wallet.domain.entity.card.BankCard;

import org.immutables.gson.Gson;
import org.immutables.value.Value;

import io.techery.janet.smartcard.model.Record;

@Value.Immutable
@Gson.TypeAdapters
public abstract class RecordIssuerInfo {

   @Value.Default
   public String bankName() {
      return "";
   }

   //TODO: financialService should be null
   @Value.Default
   public Record.FinancialService financialService() {
      return Record.FinancialService.MASTERCARD;
   }

   @Value.Default
   public BankCard.CardType cardType() {
      return BankCard.CardType.UNKNOWN;
   }
}
