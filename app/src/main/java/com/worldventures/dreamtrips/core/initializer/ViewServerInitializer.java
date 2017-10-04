package com.worldventures.dreamtrips.core.initializer;

import android.app.Application;

import com.techery.spares.utils.ui.ViewServer;
import com.worldventures.core.di.AppInitializer;
import com.worldventures.core.janet.Injector;
import com.worldventures.dreamtrips.BuildConfig;

import javax.inject.Inject;


public class ViewServerInitializer implements AppInitializer {

   @Inject protected Application app;

   @Override
   public void initialize(Injector injector) {
      if (!BuildConfig.DEBUG) return;
      //
      injector.inject(this);
      app.registerActivityLifecycleCallbacks(new ViewServer.ViewServerLifecycleDelegate());
   }
}
