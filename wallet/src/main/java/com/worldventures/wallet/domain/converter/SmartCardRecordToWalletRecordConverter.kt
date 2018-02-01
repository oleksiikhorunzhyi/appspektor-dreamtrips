package com.worldventures.wallet.domain.converter

import com.worldventures.core.converter.Converter
import com.worldventures.wallet.domain.bankName
import com.worldventures.wallet.domain.entity.SDKRecord
import com.worldventures.wallet.domain.entity.record.Record
import com.worldventures.wallet.util.WalletRecordUtil
import io.techery.mappery.MapperyContext

class SmartCardRecordToWalletRecordConverter : Converter<SDKRecord, Record> {

   override fun sourceClass() = SDKRecord::class.java

   override fun targetClass() = Record::class.java

   override fun convert(context: MapperyContext, source: SDKRecord) =
         Record(
               id = source.id()?.toString(),
               number = source.cardNumber(),
               numberLastFourDigits = WalletRecordUtil.obtainLastCardDigits(source.cardNumber()),
               expDate = source.expDate(),
               cvv = source.cvv(),
               track1 = source.t1(),
               track2 = source.t2(),
               track3 = source.t3(),
               nickname = source.title(),
               bankName = source.bankName(),
               financialService = source.financialService().toDomainFinancialService(),
               recordType = source.cardType().toDomainRecordType(),
               cardHolderFirstName = source.firstName(),
               cardHolderMiddleName = source.middleName(),
               cardHolderLastName = source.lastName(),
               version = source.version()
         )
}
