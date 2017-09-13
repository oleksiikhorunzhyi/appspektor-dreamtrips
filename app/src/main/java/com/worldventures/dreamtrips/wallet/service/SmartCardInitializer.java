package com.worldventures.dreamtrips.wallet.service;

import com.techery.spares.application.AppInitializer;
import com.techery.spares.module.Injector;
import com.worldventures.dreamtrips.wallet.service.lostcard.LocationTrackingManager;

import javax.inject.Inject;


public class SmartCardInitializer implements AppInitializer {
   @Inject LocationTrackingManager locationTrackingManager;

   @Override
   public void initialize(Injector injector) {
      injector.inject(this);
      locationTrackingManager.track();
   }
}
