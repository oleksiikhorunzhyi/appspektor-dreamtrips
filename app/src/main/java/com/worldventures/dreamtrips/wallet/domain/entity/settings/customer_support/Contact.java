package com.worldventures.dreamtrips.wallet.domain.entity.settings.customer_support;

import org.immutables.gson.Gson;
import org.immutables.value.Value;

@Gson.TypeAdapters
@Value.Immutable
public abstract class Contact {

   @Value.Default
   public String contactAddress() {
      return "";
   }

   @Value.Default
   public String email() {
      return "";
   }

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
