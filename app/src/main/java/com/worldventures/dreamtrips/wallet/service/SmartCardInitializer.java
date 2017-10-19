package com.worldventures.dreamtrips.wallet.service;

import com.worldventures.core.di.AppInitializer;
import com.worldventures.core.janet.Injector;
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
