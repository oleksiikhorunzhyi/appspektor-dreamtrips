package com.worldventures.dreamtrips.core.initializer;

import com.worldventures.core.di.AppInitializer;
import com.worldventures.core.janet.Injector;
import com.worldventures.dreamtrips.BuildConfig;

import timber.log.Timber;

public class LoggingInitializer implements AppInitializer {

   @Override
   public void initialize(Injector injector) {
      if (BuildConfig.DEBUG) {
         Timber.plant(new Timber.DebugTree());
      }
   }
}
