package com.worldventures.dreamtrips.wallet.domain.entity;

import android.support.annotation.Nullable;

import org.immutables.value.Value;

@Value.Immutable
public abstract class SmartCard {

   public abstract String smartCardId();

   // TODO: 2/17/17 it's field is really need ?
   public abstract CardStatus cardStatus();

   @Value.Default
   public String deviceAddress() {
      return "";
   }

   @Nullable
   public abstract String deviceId();

   @Value.Default
   public String serialNumber() {
      return "";
   }

   @Value.Default
   public SmartCardUser user() {
      //todo: migration-hack in the past
      return ImmutableSmartCardUser.builder().firstName("").build();
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
      ACTIVE, INACTIVE, DRAFT
   }
}
