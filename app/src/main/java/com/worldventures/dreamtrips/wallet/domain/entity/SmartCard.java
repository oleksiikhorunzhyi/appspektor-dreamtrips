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
   public String sdkVersion() {
      return "";
   }

   public enum CardStatus {
      ACTIVE, IN_PROVISIONING;

      public boolean isActive() {
         return this == ACTIVE;
      }
   }
}
