package com.worldventures.dreamtrips.core.initializer;

import com.worldventures.core.di.AppInitializer;
import com.worldventures.core.janet.Injector;
import com.worldventures.dreamtrips.core.utils.BadgeCountObserver;

import javax.inject.Inject;


public class BadgeCountObserverInitializer implements AppInitializer {

   @Inject BadgeCountObserver badgeCountObserver;

   @Override
   public void initialize(Injector injector) {
      injector.inject(this);
   }
}
