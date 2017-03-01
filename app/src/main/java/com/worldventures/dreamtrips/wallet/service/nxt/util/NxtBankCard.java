package com.worldventures.dreamtrips.wallet.service.nxt.util;

import android.support.annotation.NonNull;

import com.worldventures.dreamtrips.wallet.domain.entity.card.BankCard;
import com.worldventures.dreamtrips.wallet.service.nxt.model.MultiErrorResponse;

import java.util.List;

public interface NxtBankCard {

   BankCard getTokenizedBankCard();

   BankCard getDetokenizedBankCard();

   @NonNull
   List<MultiErrorResponse> getResponseErrors();

}