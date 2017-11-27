package com.worldventures.wallet.domain.entity

enum class ConnectionStatus {
   CONNECTED, DFU, DISCONNECTED;

   val isConnected: Boolean
      get() = this == CONNECTED
}
