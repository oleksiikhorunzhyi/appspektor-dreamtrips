package com.worldventures.dreamtrips.wallet.domain.entity;

import android.support.annotation.Nullable;

import org.immutables.value.Value;

@Value.Immutable
public abstract class SmartCard {

   public abstract String smartCardId();

   public abstract CardStatus cardStatus();

   /**
    * this method is not used in application
    * also we have {@link #cardName()} method
    */
   @Deprecated
   public abstract String deviceName();

   @Value.Default
   public String deviceAddress() {
      return "";
   }

   @Value.Default
   public String sdkVersion() {
      return "";
   }

   @Value.Default
   public String firmWareVersion() {
      return "";
   }

   @Value.Default
   public String serialNumber() {
      return "";
   }

   @Value.Default
   public ConnectionStatus connectionStatus() {
      return ConnectionStatus.DISCONNECTED;
   }

   @Value.Default
   public String cardName() {
      return "";
   }

   @Value.Default
   public boolean stealthMode() {
      return false;
   }

   @Value.Default
   public boolean lock() {
      return false;
   }

   @Nullable
   public abstract String userPhoto();

   @Value.Default
   public int batteryLevel() {
      return 0;
   }

   @Value.Default
   public long disableCardDelay() {
      return 0; //never
   }

   @Value.Default
   public long clearFlyeDelay() {
      return 0; //never
   }

   public enum CardStatus {
      ACTIVE, INACTIVE, DRAFT
   }

   public enum ConnectionStatus {
      CONNECTED, DFU, DISCONNECTED, ERROR;

      public boolean isConnected() {
         return this == CONNECTED;
      }
   }
}
