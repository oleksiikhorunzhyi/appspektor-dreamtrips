package com.worldventures.wallet.domain.entity

import java.util.Date

data class SmartCardDetails(
      val deviceId: String?,
      // TODO Remove unused fields (they were used in SendWalletFeedbackCommand)
      val serialNumber: String = "",
      val bleAddress: String = "",
      val wvOrderId: String = "",
      val revVersion: String = "",
      val nxtOrderId: String = "",
      val orderDate: Date = Date())
