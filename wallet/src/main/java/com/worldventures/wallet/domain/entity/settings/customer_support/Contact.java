package com.worldventures.wallet.domain.entity.settings.customer_support;

import org.immutables.gson.Gson;
import org.immutables.value.Value;
import org.jetbrains.annotations.Nullable;

@Gson.TypeAdapters
@Value.Immutable
public abstract class Contact {

   @Nullable
   @Value.Default
   public String contactAddress() {
      return "";
   }

   @Value.Default
   public String email() {
      return "";
   }

   @Nullable
   @Value.Default
   public String formattedAddress() {
      return "";
   }

   @Value.Default
   public String phone() {
      return "";
   }

   @Value.Default
   public String fax() {
      return "";
   }
}
