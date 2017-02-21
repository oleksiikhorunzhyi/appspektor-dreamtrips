package com.worldventures.dreamtrips.wallet.domain.entity;

import android.support.annotation.Nullable;

import org.immutables.value.Value;

@Value.Immutable
public abstract class SmartCard {

   public abstract String smartCardId();

   public abstract CardStatus cardStatus();

   @Nullable
   public abstract String deviceId();

   @Value.Default
   @Deprecated
   public String deviceAddress() {
      return "";
   }

   @Deprecated
   @Value.Default
   public String serialNumber() {
      return "";
   }

   @Value.Default
   public String sdkVersion() {
      return "";
   }

   @Deprecated
   @Nullable
   public abstract SmartCardFirmware firmwareVersion();

   @Deprecated
   @Value.Default
   public boolean stealthMode() {
      return false;
   }

   @Deprecated
   @Value.Default
   public long disableCardDelay() {
      return 0; //never
   }

   @Deprecated
   @Value.Default
   public long clearFlyeDelay() {
      return 0; //never
   }

   public enum CardStatus {
      ACTIVE, IN_PROVISIONING
   }
}
