package com.worldventures.dreamtrips.core.initializer;

import com.techery.spares.application.AppInitializer;
import com.techery.spares.module.Injector;
import com.worldventures.dreamtrips.core.utils.tracksystem.Tracker;
import com.worldventures.dreamtrips.core.utils.tracksystem.TrackingHelper;

import java.util.Set;

import javax.inject.Inject;

public class AnalyticsInitializer implements AppInitializer {

   @Inject Set<Tracker> trackers;

   @Override
   public void initialize(Injector injector) {
      injector.inject(this);
      TrackingHelper.init(trackers);
   }
}
