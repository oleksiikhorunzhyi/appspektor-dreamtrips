package com.worldventures.dreamtrips.wallet.domain.entity;

import org.immutables.value.Value;

@Value.Style(visibility = Value.Style.ImplementationVisibility.PACKAGE)
@Value.Immutable
public abstract class SmartCardUserPhone {

   public static SmartCardUserPhone of(String code, String number) {
      return ImmutableSmartCardUserPhone.of(code, number);
   }

   @Value.Parameter
   public abstract String code();

   @Value.Parameter
   public abstract String number();

   @Value.Derived
   public String fullPhoneNumber() {
      return code() + number();
   }
}
