package com.worldventures.wallet.domain.entity

data class SmartCardStatus(
      val connectionStatus: ConnectionStatus = ConnectionStatus.DISCONNECTED,
      val stealthMode: Boolean = false,
      val lock: Boolean = false,
      val batteryLevel: Int = 0,
      val disableCardDelay: Long = 0,
      val clearFlyeDelay: Long = 0
)
