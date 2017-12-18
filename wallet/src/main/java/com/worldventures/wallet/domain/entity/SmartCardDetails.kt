package com.worldventures.wallet.domain.entity

import java.util.Date

data class SmartCardDetails(
      val serialNumber: String,
      val smartCardId: Long,
      val bleAddress: String,
      val wvOrderId: String,
      val revVersion: String,
      val nxtOrderId: String,
      val orderDate: Date)
