package com.worldventures.dreamtrips.wallet.model

import com.worldventures.dreamtrips.api.smart_card.user_association.model.SmartCardDetails
import java.util.*

open class TestSmartCardDetails(private val smartCardId: Long) : SmartCardDetails {

   override fun scID(): Long = smartCardId

   override fun serialNumber() = ""

   override fun bleAddress() = "DA:30:55:CF:B4:9E"

   override fun wvOrderId() = "123"

   override fun revVersion() = "123"

   override fun nxtOrderId() = "123"

   override fun orderDate() = Date()
}