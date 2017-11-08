package com.worldventures.wallet.domain.converter

import com.worldventures.core.converter.Converter
import com.worldventures.wallet.domain.entity.SDKCardType
import com.worldventures.wallet.domain.entity.record.RecordType

import io.techery.mappery.MapperyContext

class SmartCardRecordTypeToWalletRecordTypeConverter : Converter<SDKCardType, RecordType> {

   override fun sourceClass() = SDKCardType::class.java

   override fun targetClass() = RecordType::class.java

   override fun convert(context: MapperyContext, source: SDKCardType): RecordType {
      return when (source) {
         SDKCardType.CREDIT -> RecordType.CREDIT
         SDKCardType.DEBIT -> RecordType.DEBIT
         SDKCardType.PREFERENCE -> RecordType.PREFERENCE
         else -> RecordType.FINANCIAL
      }
   }
}
