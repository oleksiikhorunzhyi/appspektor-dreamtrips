package com.worldventures.dreamtrips.wallet.service.nxt.util;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.worldventures.dreamtrips.wallet.domain.entity.card.BankCard;
import com.worldventures.dreamtrips.wallet.service.nxt.model.MultiResponseBody;

import java.util.Collections;
import java.util.List;

public class TokenizedBankCard extends NxtBankCardResponse {

   public static TokenizedBankCard from(@NonNull BankCard detokenizedCard, @NonNull MultiResponseBody nxtResponses) {
      return from(detokenizedCard, Collections.singletonList(nxtResponses), null);
   }

   public static TokenizedBankCard from(@NonNull BankCard detokenizedCard, @NonNull List<MultiResponseBody> nxtResponses) {
      return from(detokenizedCard, nxtResponses, null);
   }

   public static TokenizedBankCard from(@NonNull BankCard detokenizedCard, @NonNull List<MultiResponseBody> nxtResponses, String refIdPrefix) {
      return new TokenizedBankCard(detokenizedCard, nxtResponses, refIdPrefix);
   }

   private TokenizedBankCard(@NonNull BankCard detokenizedCard, @NonNull List<MultiResponseBody> nxtResponses, @Nullable String refIdPrefix) {
      super(detokenizedCard, nxtResponses, refIdPrefix);
   }

   @Override
   public BankCard getTokenizedBankCard() {
      return NxtBankCardHelper.getTokenizedBankCard(this, refIdPrefix);
   }

   @Override
   public BankCard getDetokenizedBankCard() {
      return bankCard;
   }

}