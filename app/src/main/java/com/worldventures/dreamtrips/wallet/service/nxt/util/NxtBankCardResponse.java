package com.worldventures.dreamtrips.wallet.service.nxt.util;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.worldventures.dreamtrips.wallet.domain.entity.card.BankCard;
import com.worldventures.dreamtrips.wallet.service.nxt.model.MultiErrorResponse;
import com.worldventures.dreamtrips.wallet.service.nxt.model.MultiResponseBody;
import com.worldventures.dreamtrips.wallet.service.nxt.model.MultiResponseElement;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public abstract class NxtBankCardResponse implements NxtBankCard {

   protected final BankCard bankCard;
   protected final Map<String, String> nxtValues = new HashMap<>();
   protected final Map<String, MultiErrorResponse> nxtErrors = new HashMap<>();

   @Nullable
   protected final String refIdPrefix;

   protected NxtBankCardResponse(@NonNull BankCard bankCard, @NonNull List<MultiResponseBody> nxtResponses, @Nullable String refIdPrefix) {
      this.bankCard = bankCard;
      this.refIdPrefix = refIdPrefix;
      for (MultiResponseBody body : nxtResponses) {
         for (MultiResponseElement element : body.multiResponseElements()) {
            nxtValues.put(element.referenceId(), element.value());
            nxtErrors.put(element.referenceId(), element.error());
         }
      }
   }

   @NonNull
   @Override
   public List<MultiErrorResponse> getResponseErrors() {
      return NxtBankCardHelper.getResponseErrors(this, refIdPrefix);
   }

}