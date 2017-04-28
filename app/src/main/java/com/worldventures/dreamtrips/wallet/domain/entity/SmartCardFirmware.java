package com.worldventures.dreamtrips.wallet.domain.entity;

import org.immutables.value.Value;
import org.jetbrains.annotations.Nullable;

@Value.Immutable
public abstract class SmartCardFirmware {

   @Nullable
   public abstract String firmwareBundleVersion();

   @Value.Default
   public String nordicAppVersion() {
      return "";
   }

   @Value.Default
   public String nrfBootloaderVersion() {
      return "";
   }

   @Value.Default
   public String internalAtmelVersion() {
      return "";
   }

   @Value.Default
   public String internalAtmelBootloaderVersion() {
      return "";
   }

   @Value.Default
   public String externalAtmelVersion() {
      return "";
   }

   @Value.Default
   public String externalAtmelBootloaderVersion() {
      return "";
   }

}
