package com.worldventures.dreamtrips.wallet.domain.entity;

import android.support.annotation.Nullable;

import org.immutables.value.Value;

@Value.Immutable
public abstract class SmartCardUser {

   public abstract String firstName();

   @Value.Default
   public String lastName() {
      return "";
   }

   @Value.Default
   public String middleName() {
      return "";
   }

   @Nullable
   public abstract SmartCardUserPhone phoneNumber();

   @Nullable
   public abstract SmartCardUserPhoto userPhoto();
}