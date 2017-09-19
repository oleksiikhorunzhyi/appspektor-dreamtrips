package com.worldventures.dreamtrips.core.initializer;

import com.techery.spares.application.AppInitializer;
import com.techery.spares.module.Injector;
import com.worldventures.dreamtrips.modules.common.delegate.GlobalAnalyticEventHandler;

import javax.inject.Inject;

public class AnalyticsInitializer implements AppInitializer {

   @Inject GlobalAnalyticEventHandler eventHandler;

   @Override
   public void initialize(Injector injector) {
      // force GlobalAnalyticEventHandler instance creation
      injector.inject(this);
   }
}
