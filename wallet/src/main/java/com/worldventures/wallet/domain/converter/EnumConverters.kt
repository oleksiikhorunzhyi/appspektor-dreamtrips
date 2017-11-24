package com.worldventures.wallet.domain.converter

import com.worldventures.wallet.domain.entity.SDKCardType
import com.worldventures.wallet.domain.entity.SDKFinancialService
import com.worldventures.wallet.domain.entity.record.FinancialService
import com.worldventures.wallet.domain.entity.record.RecordType
import io.techery.janet.smartcard.model.Record


fun RecordType.toSDKRecordType(): SDKCardType =
      when (this) {
         RecordType.CREDIT -> SDKCardType.CREDIT
         RecordType.DEBIT -> SDKCardType.DEBIT
         RecordType.PREFERENCE -> SDKCardType.PREFERENCE
         else -> SDKCardType.FINANCIAL
      }

fun SDKCardType.toDomainRecordType(): RecordType =
      when (this) {
         SDKCardType.CREDIT -> RecordType.CREDIT
         SDKCardType.DEBIT -> RecordType.DEBIT
         SDKCardType.PREFERENCE -> RecordType.PREFERENCE
         else -> RecordType.FINANCIAL
      }

fun SDKFinancialService.toDomainFinancialService(): FinancialService =
      when (this) {
         Record.FinancialService.VISA -> FinancialService.VISA
         Record.FinancialService.MASTERCARD -> FinancialService.MASTERCARD
         Record.FinancialService.DISCOVER -> FinancialService.DISCOVER
         Record.FinancialService.AMEX -> FinancialService.AMEX
         else  -> FinancialService.GENERIC
      }

fun FinancialService.toSDKFinancialService(): SDKFinancialService =
      when (this) {
         FinancialService.VISA -> SDKFinancialService.VISA
         FinancialService.MASTERCARD -> SDKFinancialService.MASTERCARD
         FinancialService.DISCOVER -> SDKFinancialService.DISCOVER
         FinancialService.AMEX -> SDKFinancialService.AMEX
         else -> SDKFinancialService.GENERIC
      }
