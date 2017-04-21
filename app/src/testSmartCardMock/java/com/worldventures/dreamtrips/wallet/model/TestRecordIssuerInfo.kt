package com.worldventures.dreamtrips.wallet.model

import com.worldventures.dreamtrips.wallet.domain.entity.FinancialService
import com.worldventures.dreamtrips.wallet.domain.entity.RecordIssuerInfo
import com.worldventures.dreamtrips.wallet.domain.entity.card.BankCard

class TestRecordIssuerInfo(
      private val cardType: BankCard.CardType = BankCard.CardType.UNKNOWN,
      private val financialService: FinancialService = FinancialService.MASTERCARD,
      private val bankName: String = ""
) : RecordIssuerInfo() {

   override fun cardType() = cardType

   override fun financialService() = financialService

   override fun bankName() = bankName
}