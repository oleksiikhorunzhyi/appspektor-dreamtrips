package com.worldventures.dreamtrips.wallet.domain.entity;

import android.support.annotation.Nullable;

import org.immutables.value.Value;

import java.io.File;

@Value.Immutable
public abstract class SmartCard {

   public abstract String smartCardId();

   public abstract CardStatus cardStatus();

   @Value.Default
   public String deviceAddress() {
      return "";
   }

   @Value.Default
   public SmartCardUser user() {
      //need for migration-hack
      return ImmutableSmartCardUser.builder()
            .userPhoto(ImmutableSmartCardUserPhoto.builder()
                  .original(userPhoto() != null ? new File(userPhoto()) : null)
                  .build())
            .firstName(cardName()).lastName("")
            .build();
   }

   @Value.Default
   public String sdkVersion() {
      return "";
   }

   @Nullable
   public abstract SmartCardFirmware firmwareVersion();

   @Value.Default
   public String serialNumber() {
      return "";
   }

   @Value.Default
   public ConnectionStatus connectionStatus() {
      return ConnectionStatus.DISCONNECTED;
   }

   //legacy, now you need request fullname from user()
   @Value.Default
   @Deprecated
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

   //legacy, now you need request photo from user()
   @Nullable
   @Deprecated
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

   @Nullable
   public abstract String deviceId();

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
