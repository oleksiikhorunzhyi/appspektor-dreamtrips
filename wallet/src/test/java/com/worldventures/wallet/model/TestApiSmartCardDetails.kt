package com.worldventures.wallet.model

import com.worldventures.wallet.domain.entity.ApiSmartCardDetails
import java.util.Date

open class TestApiSmartCardDetails(private val smartCardId: Long) : ApiSmartCardDetails {

   override fun scID(): Long = smartCardId

   override fun serialNumber() = ""

   override fun bleAddress() = "DA:30:55:CF:B4:9E"

   override fun wvOrderId() = "123"

   override fun revVersion() = "123"

   override fun nxtOrderId() = "123"

   override fun orderDate() = Date()
}
