package com.worldventures.wallet.domain.entity;

import org.immutables.value.Value;

import java.util.Locale;

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
      return String.format(Locale.US, "%s%s", code(), number());
   }
}
