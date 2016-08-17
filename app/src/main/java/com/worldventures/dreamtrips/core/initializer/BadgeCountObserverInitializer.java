package com.worldventures.dreamtrips.core.initializer;

import com.techery.spares.application.AppInitializer;
import com.techery.spares.module.Injector;
import com.worldventures.dreamtrips.core.utils.BadgeCountObserver;

import javax.inject.Inject;


public class BadgeCountObserverInitializer implements AppInitializer {

   @Inject BadgeCountObserver badgeCountObserver;

   @Override
   public void initialize(Injector injector) {
      injector.inject(this);
   }
}
