package com.worldventures.dreamtrips.wallet.service.nxt.util;

import com.worldventures.dreamtrips.wallet.domain.entity.card.BankCard;

public interface NxtBankCard {

   BankCard getTokenizedBankCard();

   BankCard getDetokenizedBankCard();

}