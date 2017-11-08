package com.worldventures.wallet.domain.entity.record

data class Record(
      val id: String?,
      val number: String,
      val numberLastFourDigits: String,
      val expDate: String,
      val track1: String? = null,
      val track2: String? = null,
      val track3: String? = null,
      val cardHolderFirstName: String = "",
      val cardHolderMiddleName: String = "",
      val cardHolderLastName: String = "",
      val nickname: String = "",
      val bankName: String = "",
      val cvv: String = "",
      val version: String = "",
      val financialService: FinancialService = FinancialService.GENERIC,
      val recordType: RecordType = RecordType.FINANCIAL) {

   override fun equals(other: Any?): Boolean {
      if (super.equals(other)) {
         return true
      }
      if (other is Record) {
         val record = other as Record?
         val recordId = record!!.id
         val id = id
         return recordId != null && id != null && recordId == id
      }
      return false
   }

   override fun hashCode(): Int {
      return id?.hashCode() ?: super.hashCode()
   }
}
