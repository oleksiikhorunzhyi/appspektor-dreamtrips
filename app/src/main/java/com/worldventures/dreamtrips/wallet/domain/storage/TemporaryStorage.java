package com.worldventures.dreamtrips.wallet.domain.storage;

import javax.inject.Inject;
import javax.inject.Singleton;

@Singleton
public class TemporaryStorage {

   private boolean newFirmwareIsAvailable;
   private boolean firmwareIsCompatible = true;

   @Inject TemporaryStorage() {
   }

   public boolean newFirmwareIsAvailable() {
      return newFirmwareIsAvailable;
   }

   public void newFirmwareIsAvailable(boolean newFirmwareIsAvailable) {
      this.newFirmwareIsAvailable = newFirmwareIsAvailable;
   }

   public boolean firmwareIsCompatible() {
      return firmwareIsCompatible;
   }

   public void firmwareIsCompatible(boolean firmwareIsCompatible) {
      this.firmwareIsCompatible = firmwareIsCompatible;
   }
}
