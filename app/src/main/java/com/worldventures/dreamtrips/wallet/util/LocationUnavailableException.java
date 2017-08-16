package com.worldventures.dreamtrips.wallet.util;

import com.google.android.gms.location.LocationSettingsResult;

public class LocationUnavailableException extends RuntimeException {

   private final LocationSettingsResult locationSettingsResult;

   public LocationUnavailableException(LocationSettingsResult locationSettingsResult) {
      this.locationSettingsResult = locationSettingsResult;
   }

   public LocationSettingsResult getLocationSettingsResult() {
      return locationSettingsResult;
   }
}
