package com.worldventures.dreamtrips.wallet.service.nxt.util;

import android.support.annotation.NonNull;

import com.worldventures.dreamtrips.wallet.domain.entity.card.BankCard;
import com.worldventures.dreamtrips.wallet.service.nxt.model.MultiResponseBody;

public class TokenizedBankCard extends NxtBankCardResponse {

   public static TokenizedBankCard from(@NonNull BankCard detokenizedCard, @NonNull MultiResponseBody nxtResponse) {
      return new TokenizedBankCard(detokenizedCard, nxtResponse);
   }

   private TokenizedBankCard(@NonNull BankCard detokenizedCard, @NonNull MultiResponseBody nxtResponse) {
      super(detokenizedCard, nxtResponse);
   }

   @Override
   public BankCard getTokenizedBankCard() {
      return NxtBankCardHelper.getTokenizedBankCard(this);
   }

   @Override
   public BankCard getDetokenizedBankCard() {
      return bankCard;
   }

}