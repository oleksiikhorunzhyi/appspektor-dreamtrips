package com.worldventures.wallet.domain.converter

import com.worldventures.core.converter.Converter
import com.worldventures.wallet.domain.entity.SDKFinancialService
import com.worldventures.wallet.domain.entity.record.FinancialService

import io.techery.janet.smartcard.model.Record
import io.techery.mappery.MapperyContext

class WalletFinancialServiceToSmartCardFinancialServiceConverter : Converter<FinancialService, SDKFinancialService> {

   override fun sourceClass() = FinancialService::class.java

   override fun targetClass() = SDKFinancialService::class.java

   override fun convert(context: MapperyContext, source: FinancialService): Record.FinancialService {
      return when (source) {
         FinancialService.VISA -> SDKFinancialService.VISA
         FinancialService.MASTERCARD -> SDKFinancialService.MASTERCARD
         FinancialService.DISCOVER -> SDKFinancialService.DISCOVER
         FinancialService.AMEX -> SDKFinancialService.AMEX
         else -> SDKFinancialService.GENERIC
      }
   }
}
