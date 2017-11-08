package com.worldventures.wallet.domain.converter

import com.worldventures.core.converter.Converter
import com.worldventures.wallet.domain.entity.SDKCardType
import com.worldventures.wallet.domain.entity.record.RecordType

import io.techery.mappery.MapperyContext

class WalletRecordTypeToSmartCardRecordTypeConverter : Converter<RecordType, SDKCardType> {

   override fun sourceClass() = RecordType::class.java

   override fun targetClass() = SDKCardType::class.java

   override fun convert(context: MapperyContext, source: RecordType): SDKCardType {
      return when (source) {
         RecordType.CREDIT -> SDKCardType.CREDIT
         RecordType.DEBIT -> SDKCardType.DEBIT
         RecordType.PREFERENCE -> SDKCardType.PREFERENCE
         else -> SDKCardType.FINANCIAL
      }
   }
}
