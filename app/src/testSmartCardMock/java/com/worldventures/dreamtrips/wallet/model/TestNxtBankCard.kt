package com.worldventures.dreamtrips.wallet.model

import com.worldventures.dreamtrips.wallet.domain.entity.card.BankCard
import com.worldventures.dreamtrips.wallet.service.nxt.model.ImmutableMultiResponseBody
import com.worldventures.dreamtrips.wallet.service.nxt.model.MultiResponseBody
import com.worldventures.dreamtrips.wallet.service.nxt.util.NxtBankCardResponse

/**
 * Created by shliama on 2/13/17.
 */
class TestNxtBankCard(bankCard: BankCard, response: MultiResponseBody = ImmutableMultiResponseBody.builder().build())
   : NxtBankCardResponse(bankCard, response) {

   override fun getTokenizedBankCard(): BankCard = bankCard

   override fun getDetokenizedBankCard(): BankCard = bankCard

}