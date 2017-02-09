package com.worldventures.dreamtrips.wallet.service.nxt.util;

import android.support.annotation.NonNull;

import com.worldventures.dreamtrips.wallet.domain.entity.card.BankCard;
import com.worldventures.dreamtrips.wallet.service.nxt.model.MultiResponseBody;

public class DetokenizedBankCard extends NxtBankCardResponse {

   public static DetokenizedBankCard from(@NonNull BankCard tokenizedCard, @NonNull MultiResponseBody nxtResponse) {
      return new DetokenizedBankCard(tokenizedCard, nxtResponse);
   }

   private DetokenizedBankCard(@NonNull BankCard tokenizedCard, @NonNull MultiResponseBody nxtResponse) {
      super(tokenizedCard, nxtResponse);
   }

   @Override
   public BankCard getTokenizedBankCard() {
      return bankCard;
   }

   @Override
   public BankCard getDetokenizedBankCard() {
      return NxtBankCardHelper.getDetokenizedBankCard(this);
   }

}