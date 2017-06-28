package com.worldventures.dreamtrips.wallet.domain.entity;

public enum ConnectionStatus {
   CONNECTED, DFU, DISCONNECTED;

   public boolean isConnected() {
      return this == CONNECTED;
   }
}