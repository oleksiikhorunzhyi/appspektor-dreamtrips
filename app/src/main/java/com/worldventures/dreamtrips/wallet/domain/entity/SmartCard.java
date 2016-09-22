package com.worldventures.dreamtrips.wallet.domain.entity;

import android.support.annotation.Nullable;

import org.immutables.value.Value;

@Value.Immutable
public abstract class SmartCard {

   @Value.Default
   public String currentSdkVersion() {
      return "0.0.1"; //todo temp
   }

   public abstract String smartCardId();

   public abstract CardStatus cardStatus();

   public abstract String deviceName();

   public abstract String deviceAddress();

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
      CONNECTED, DISCONNECTED, ERROR
   }
}
