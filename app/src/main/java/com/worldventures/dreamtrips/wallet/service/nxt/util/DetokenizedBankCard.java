package com.worldventures.dreamtrips.wallet.service.nxt.util;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.worldventures.dreamtrips.wallet.domain.entity.card.BankCard;
import com.worldventures.dreamtrips.wallet.service.nxt.model.MultiResponseBody;

import java.util.Collections;
import java.util.List;

public class DetokenizedBankCard extends NxtBankCardResponse {

   public static DetokenizedBankCard from(@NonNull BankCard tokenizedCard, @NonNull MultiResponseBody nxtResponses) {
      return from(tokenizedCard, Collections.singletonList(nxtResponses), null);
   }

   public static DetokenizedBankCard from(@NonNull BankCard tokenizedCard, @NonNull List<MultiResponseBody> nxtResponses) {
      return new DetokenizedBankCard(tokenizedCard, nxtResponses, null);
   }

   public static DetokenizedBankCard from(@NonNull BankCard tokenizedCard, @NonNull List<MultiResponseBody> nxtResponses, String refIdPrefix) {
      return new DetokenizedBankCard(tokenizedCard, nxtResponses, refIdPrefix);
   }

   private DetokenizedBankCard(@NonNull BankCard tokenizedCard, @NonNull List<MultiResponseBody> nxtResponses, @Nullable String refIdPrefix) {
      super(tokenizedCard, nxtResponses, refIdPrefix);
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