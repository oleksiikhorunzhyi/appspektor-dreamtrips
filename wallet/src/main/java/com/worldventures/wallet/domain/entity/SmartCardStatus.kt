package com.worldventures.wallet.domain.entity

/**
 * @property firmwareVersion is version of nordic firmware. It can be empty on old firmware versions.
 * Property will be available when connectionStatus is CONNECTED.
 */
data class SmartCardStatus(
      val connectionStatus: ConnectionStatus = ConnectionStatus.DISCONNECTED,
      val stealthMode: Boolean = false,
      val lock: Boolean = false,
      val batteryLevel: Int = 0,
      val firmwareVersion: String = "",
      val disableCardDelay: Long = 0,
      val clearFlyeDelay: Long = 0
)
