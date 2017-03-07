package com.worldventures.dreamtrips.wallet.model

import com.worldventures.dreamtrips.wallet.domain.entity.AddressInfo
import com.worldventures.dreamtrips.wallet.domain.entity.record.FinancialService
import com.worldventures.dreamtrips.wallet.domain.entity.record.Record
import com.worldventures.dreamtrips.wallet.domain.entity.record.RecordType

class TestRecord(
      private val id: String?,
      private val title: String = "TEST",
      private val cardNameHolder: String = "Test Holder",
      private val number: String = "123456789",
      private val numberLastFourDigits: String = "6789",
      private val expDate: String = "07/17",
      private val cvv: String = "375",
      private val track1: String? = null,
      private val track2: String? = null,
      private val track3: String? = null,
      private val cardType: RecordType = RecordType.FINANCIAL,
      private val financialService: FinancialService = FinancialService.MASTERCARD,
      private val bankName: String = "",
      private val addressInfo: AddressInfo = TestAddressInfo()
) : Record() {

   override fun id(): String? = id

   override fun number(): String = number

   override fun numberLastFourDigits(): String = numberLastFourDigits

   override fun expDate(): String = expDate

   override fun track1(): String? = track1

   override fun track2(): String? = track2

   override fun track3(): String? = track3

   override fun cardNameHolder(): String = cardNameHolder

   override fun nickName(): String = title

   override fun cvv(): String = cvv

   override fun addressInfo() = addressInfo

   override fun recordType() = cardType

   override fun bankName() = bankName

   override fun financialService() = financialService

}
