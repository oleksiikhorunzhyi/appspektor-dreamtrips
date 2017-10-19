package com.worldventures.dreamtrips.wallet.initializer;

import com.worldventures.core.di.AppInitializer;
import com.worldventures.core.janet.Injector;
import com.worldventures.dreamtrips.wallet.service.lostcard.LocationTrackingManager;

import javax.inject.Inject;

public class LostCardInitializer implements AppInitializer {

   @Inject LocationTrackingManager trackingManager;

   @Override
   public void initialize(Injector injector) {
      injector.inject(this);
      trackingManager.track();
   }
}
