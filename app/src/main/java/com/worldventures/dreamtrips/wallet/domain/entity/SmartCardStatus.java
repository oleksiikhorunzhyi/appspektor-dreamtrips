package com.worldventures.dreamtrips.wallet.domain.entity;

import org.immutables.value.Value;

@Value.Immutable
public abstract class SmartCardStatus {
//
//   @Value.Default
//   public String sdkVersion() {
//      return "";
//   }
//
//   @Nullable
//   public abstract SmartCardFirmware firmwareVersion();

   @Value.Default
   public ConnectionStatus connectionStatus() {
      return ConnectionStatus.DISCONNECTED;
   }

   @Value.Default
   public boolean stealthMode() {
      return false;
   }

   @Value.Default
   public boolean lock() {
      return false;
   }

   @Value.Default
   public int batteryLevel() {
      return 0;
   }
//
//   @Value.Default
//   public long disableCardDelay() {
//      return 0; //never
//   }
//
//   @Value.Default
//   public long clearFlyeDelay() {
//      return 0; //never
//   }
}
