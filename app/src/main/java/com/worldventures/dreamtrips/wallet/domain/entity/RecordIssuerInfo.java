package com.worldventures.dreamtrips.wallet.domain.entity;


import com.worldventures.dreamtrips.wallet.domain.entity.card.BankCard;

import org.immutables.value.Value;
import org.jetbrains.annotations.Nullable;

import io.techery.janet.smartcard.model.Record;

@Value.Immutable
public interface RecordIssuerInfo {

   @Nullable
   String bankName();

   @Nullable
   Record.FinancialService financialService();

   @Nullable
   BankCard.CardType cardType();
}
