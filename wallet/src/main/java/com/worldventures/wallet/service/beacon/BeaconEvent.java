package com.worldventures.wallet.service.beacon;

import android.support.annotation.Nullable;

import org.altbeacon.beacon.Identifier;
import org.altbeacon.beacon.Region;

public class BeaconEvent {

   @Nullable
   private final String smartCardId;
   private final boolean enteredRegion;

   public BeaconEvent(Region region, boolean enteredRegion) {
      final Identifier smartCardIdIdentifier = region.getId3();

      this.smartCardId = smartCardIdIdentifier == null ? null : smartCardIdIdentifier.toString();
      this.enteredRegion = enteredRegion;
   }

   @Nullable
   public String getSmartCardId() {
      return smartCardId;
   }

   public boolean enteredRegion() {
      return enteredRegion;
   }

   @Override
   public String toString() {
      return "BeaconEvent{"
            + "getSmartCardId='" + smartCardId + '\''
            + ", enteredRegion=" + enteredRegion
            + '}';
   }
}
