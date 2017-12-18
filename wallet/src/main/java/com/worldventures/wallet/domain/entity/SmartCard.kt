package com.worldventures.wallet.domain.entity

data class SmartCard(
      val smartCardId: String,
      val cardStatus: CardStatus,
      val deviceId: String?)

