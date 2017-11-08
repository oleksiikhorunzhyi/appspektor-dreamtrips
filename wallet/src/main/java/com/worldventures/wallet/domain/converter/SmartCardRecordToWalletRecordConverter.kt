package com.worldventures.wallet.domain.converter

import com.worldventures.core.converter.Converter
import com.worldventures.wallet.domain.entity.SDKRecord
import com.worldventures.wallet.domain.entity.record.FinancialService
import com.worldventures.wallet.domain.entity.record.Record
import com.worldventures.wallet.domain.entity.record.RecordType
import com.worldventures.wallet.util.WalletRecordUtil

import io.techery.mappery.MapperyContext

class SmartCardRecordToWalletRecordConverter : Converter<SDKRecord, Record> {

   override fun sourceClass() = SDKRecord::class.java

   override fun targetClass() = Record::class.java

   override fun convert(context: MapperyContext, source: SDKRecord): Record {
      val metadata = source.metadata()
      //no use getOrDefault, for support < Java 8
      val bankName = metadata[WalletRecordToSmartCardRecordConverter.BANK_NAME_FIELD]

      val recordId = source.id()

      return Record(
            id = recordId?.toString(),
            number = source.cardNumber(),
            numberLastFourDigits = WalletRecordUtil.obtainLastCardDigits(source.cardNumber()),
            expDate = source.expDate(),
            cvv = source.cvv(),
            track1 = source.t1(),
            track2 = source.t2(),
            nickname = source.title(),
            bankName = bankName ?: "",
            financialService = context.convert(source.financialService(), FinancialService::class.java),
            recordType = context.convert(source.cardType(), RecordType::class.java),
            cardHolderFirstName = source.firstName(),
            cardHolderMiddleName = source.middleName(),
            cardHolderLastName = source.lastName(),
            version = source.version()
      )
   }
}
