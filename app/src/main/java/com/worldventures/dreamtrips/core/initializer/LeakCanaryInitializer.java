package com.worldventures.dreamtrips.core.initializer;

import android.os.Build;

import com.squareup.leakcanary.RefWatcher;
import com.worldventures.core.di.AppInitializer;
import com.worldventures.core.janet.Injector;

import javax.inject.Inject;

public class LeakCanaryInitializer implements AppInitializer {

   @Inject protected RefWatcher instance;

   @Override
   public void initialize(Injector injector) {
      if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) injector.inject(this);
   }
}
