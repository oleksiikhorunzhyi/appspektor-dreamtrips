package com.worldventures.dreamtrips.core.initializer;

import android.os.Build;

import com.squareup.leakcanary.RefWatcher;
import com.techery.spares.application.AppInitializer;
import com.techery.spares.module.Injector;

import javax.inject.Inject;

public class LeakCanaryInitializer implements AppInitializer {

   @Inject protected RefWatcher instance;

   @Override
   public void initialize(Injector injector) {
      if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) injector.inject(this);
   }
}
