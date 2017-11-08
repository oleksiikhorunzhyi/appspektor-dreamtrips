package com.worldventures.wallet.analytics

import com.worldventures.core.service.analytics.AttributeMap
import com.worldventures.wallet.domain.entity.record.FinancialService
import com.worldventures.wallet.domain.entity.record.Record
import java.util.HashMap

abstract class BaseCardDetailsAction : WalletAnalyticsAction() {

   @AttributeMap protected val attributeMap: MutableMap<String, String> = HashMap()

   open fun fillRecordDetails(record: Record) {

      val cardType = "Payment"
      val paymentCardType: String
      val paymentCardIssuer: String

      when (record.financialService) {
         FinancialService.AMEX -> {
            paymentCardIssuer = "American Express"
            paymentCardType = "American Express"
         }
         FinancialService.VISA -> {
            paymentCardIssuer = record.bankName
            paymentCardType = "Visa"
         }
         FinancialService.MASTERCARD -> {
            paymentCardIssuer = record.bankName
            paymentCardType = "MasterCard"
         }
         FinancialService.DISCOVER -> {
            paymentCardIssuer = "Discover"
            paymentCardType = "Discover"
         }
         else -> {
            paymentCardIssuer = "Unknown"
            paymentCardType = "Unknown"
         }
      }

      attributeMap.put("paycardtype", paymentCardType)
      attributeMap.put("paycardissuer", paymentCardIssuer)
      attributeMap.put("cardtype", cardType)
   }
}
