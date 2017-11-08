package com.worldventures.wallet.service.command.wizard

import com.worldventures.wallet.domain.entity.SmartCardUser
import com.worldventures.wallet.domain.entity.record.FinancialService.GENERIC
import com.worldventures.wallet.domain.entity.record.Record

object DummyRecordCreator {

   fun defaultRecordId(): String {
      return "0"
   }

   fun createRecords(user: SmartCardUser, version: String): List<Record> {
      val dummyCard1 = Record(
             id = "0",
             number = "9999999999994984",
             numberLastFourDigits = "4984",
             financialService = GENERIC,
             expDate = "02/19",
             cvv = "748",
             version = version,
             track1 = "B1234567890123445^FLYE/TEST CARD^23045211000000827000000",
             track2 = "1234567890123445=230452110000827",
             nickname = "Credit Card",
             cardHolderLastName = user.lastName(),
             cardHolderMiddleName = user.middleName(),
             cardHolderFirstName = user.firstName())

      val dummyCard2 = Record(
            id = "1",
            number = "9999999999999274",
            numberLastFourDigits = "9274",
            expDate = "06/21",
            cvv = "582",
            version = version,
            track1 = "B1234567890123445^FLYE/TEST CARD^23045211000000827000000",
            track2 = "1234567890123445=230452110000827",
            financialService = GENERIC,
            cardHolderLastName = user.lastName(),
            cardHolderMiddleName = user.middleName(),
            cardHolderFirstName = user.firstName(),
            nickname = "Credit Card")

      return listOf(dummyCard1, dummyCard2)
   }
}
