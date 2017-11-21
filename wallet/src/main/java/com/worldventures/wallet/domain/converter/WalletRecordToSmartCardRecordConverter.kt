package com.worldventures.wallet.domain.converter

import com.worldventures.core.converter.Converter
import com.worldventures.core.utils.ProjectTextUtils
import com.worldventures.wallet.domain.entity.SDKCardType
import com.worldventures.wallet.domain.entity.SDKFinancialService
import com.worldventures.wallet.domain.entity.SDKRecord
import com.worldventures.wallet.domain.entity.record.Record
import io.techery.janet.smartcard.model.ImmutableRecord
import io.techery.mappery.MapperyContext

class WalletRecordToSmartCardRecordConverter : Converter<Record, SDKRecord> {

   override fun sourceClass() = Record::class.java

   override fun targetClass() = SDKRecord::class.java

   override fun convert(context: MapperyContext, source: Record): SDKRecord {
      val metadata = hashMapOf(Pair(BANK_NAME_FIELD, source.bankName))

      return ImmutableRecord.builder()
            .id(parseCardId(source))
            .title(source.nickname)
            .cardNumber(source.number)
            .cvv(source.cvv)
            .expDate(source.expDate)
            .financialService(context.convert(source.financialService, SDKFinancialService::class.java))
            .t1(ProjectTextUtils.defaultIfEmpty(source.track1, ""))
            .t2(ProjectTextUtils.defaultIfEmpty(source.track2, ""))
            .t3(ProjectTextUtils.defaultIfEmpty(source.track3, ""))
            .lastName(source.cardHolderLastName)
            .firstName(source.cardHolderFirstName)
            .middleName(source.cardHolderMiddleName)
            .cardType(context.convert(source.recordType, SDKCardType::class.java))
            .version(source.version)
            .metadata(metadata)
            .build()
   }

   // RecordId is null until Record was not added on sc
   private fun parseCardId(card: Record) = if (card.id != null) Integer.parseInt(card.id) else null

   companion object {

      val BANK_NAME_FIELD = "bank_name"
   }
}
