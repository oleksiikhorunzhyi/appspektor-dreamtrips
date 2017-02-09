package com.worldventures.dreamtrips.wallet.service.nxt.util;

import android.support.annotation.NonNull;

import com.worldventures.dreamtrips.wallet.domain.entity.card.BankCard;
import com.worldventures.dreamtrips.wallet.service.nxt.model.MultiResponseBody;
import com.worldventures.dreamtrips.wallet.service.nxt.model.MultiResponseElement;

import java.util.HashMap;
import java.util.Map;

public abstract class NxtBankCardResponse implements NxtBankCard {

   protected final BankCard bankCard;
   protected final Map<String, String> nxtValues = new HashMap<>();

   protected NxtBankCardResponse(@NonNull BankCard bankCard, @NonNull MultiResponseBody nxtResponse) {
      this.bankCard = bankCard;
      for (MultiResponseElement element : nxtResponse.multiResponseElements()) {
         nxtValues.put(element.referenceId(), element.value());
         nxtValues.put(element.referenceId(), element.value());
      }
   }

}
