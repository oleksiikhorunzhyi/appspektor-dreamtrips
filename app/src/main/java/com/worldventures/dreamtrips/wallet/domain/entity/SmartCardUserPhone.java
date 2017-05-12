package com.worldventures.dreamtrips.wallet.domain.entity;

import org.immutables.value.Value;

@Value.Immutable
public abstract class SmartCardUserPhone {

   @Value.Parameter
   public abstract String number();

   @Value.Parameter
   public abstract String code();

   @Value.Derived
   public String fullPhoneNumber() {
      return code() + number();
   }
}
