package com.worldventures.dreamtrips.wallet.service.location;

import rx.Observable;

public abstract class LocationSettingsService {
   public static final String SERVICE_NAME = "com.worldventures.dreamtrips.wallet.service.location.LocationSettingsService";

   public abstract Observable<EnableResult> enableLocationApi();

   public enum EnableResult {
      AVAILABLE, UNAVAILABLE
   }

}
