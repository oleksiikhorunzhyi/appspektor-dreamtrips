package com.worldventures.wallet.domain.entity.lostcard

import java.util.*

data class WalletLocation(
      val coordinates: WalletCoordinates,
      val createdAt: Date,
      val type: WalletLocationType,
      val postedAt: Date? = null)
