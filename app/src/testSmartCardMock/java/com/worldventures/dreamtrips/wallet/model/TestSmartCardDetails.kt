package com.worldventures.dreamtrips.wallet.model

import com.worldventures.dreamtrips.wallet.domain.entity.SmartCardDetails
import java.util.*

class TestSmartCardDetails(private val smartCardId: Long) : SmartCardDetails {

   override fun serialNumber() = ""

   override fun smartCardId(): Long = smartCardId

   override fun bleAddress() = "DA:30:55:CF:B4:9E"

   override fun wvOrderId() = "123"

   override fun revVersion() = "123"

   override fun nxtOrderId() = "123"

   override fun orderDate() = Date()
}