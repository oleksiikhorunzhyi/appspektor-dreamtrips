package com.worldventures.dreamtrips.wallet.model

import com.worldventures.dreamtrips.wallet.domain.entity.AddressInfo
import com.worldventures.dreamtrips.wallet.domain.entity.RecordIssuerInfo
import com.worldventures.dreamtrips.wallet.domain.entity.card.BankCard

class TestBankCard(
      private val id: String?,
      private val issuerInfo: RecordIssuerInfo,
      private val title: String = "TEST",
      private val number: Long = 123456789L,
      private val expiryMonth: Int = 12,
      private val expiryYear: Int = 34,
      private val cvv: Int = 375,
      private val track1: String? = null,
      private val track2: String? = null,
      private val addressInfo: AddressInfo = TestAddressInfo()
      ): BankCard() {

   override fun id(): String? = id

   override fun number(): Long = number

   override fun expiryMonth(): Int = expiryMonth

   override fun expiryYear(): Int = expiryYear

   override fun track1(): String? = track1

   override fun track2(): String? = track2


   override fun title(): String= title

   override fun cvv(): Int = cvv

   override fun addressInfo() = addressInfo

   override fun issuerInfo() = issuerInfo
}