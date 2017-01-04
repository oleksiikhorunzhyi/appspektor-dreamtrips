package com.worldventures.dreamtrips.wallet.domain.entity;

import org.immutables.gson.Gson;
import org.immutables.value.Value;

@Value.Immutable
@Gson.TypeAdapters
public abstract class AddressInfo {

   @Value.Default
   public String address1() {
      return "";
   }

   @Value.Default
   public String address2() {
      return "";
   }

   @Value.Default
   public String city() {
      return "";

   }

   @Value.Default
   public String state() {
      return "";
   }

   @Value.Default
   public String zip() {
      return "";
   }
}
