package com.worldventures.dreamtrips.wallet.domain.entity;

import android.support.annotation.Nullable;

import org.immutables.value.Value;

@Value.Immutable
public abstract class SmartCard {

   public abstract String smartCardId();

   public abstract CardStatus cardStatus();

   public abstract String deviceName();

   public abstract String deviceAddress();

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

   public enum CardStatus {
      ACTIVE, INACTIVE, DRAFT
   }
}
