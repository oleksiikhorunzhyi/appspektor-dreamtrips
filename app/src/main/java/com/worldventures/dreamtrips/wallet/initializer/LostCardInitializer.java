package com.worldventures.dreamtrips.wallet.initializer;

import com.techery.spares.application.AppInitializer;
import com.techery.spares.module.Injector;
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
