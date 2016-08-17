package com.worldventures.dreamtrips.core.initializer;

import com.techery.spares.application.AppInitializer;
import com.techery.spares.module.Injector;
import com.worldventures.dreamtrips.BuildConfig;
import com.worldventures.dreamtrips.core.utils.DTTimberDebugHook;

import rx.plugins.RxJavaPlugins;

public class RxJavaLoggingInitializer implements AppInitializer {

   @Override
   public void initialize(Injector injector) {
      if (BuildConfig.RX_DEBUG_HOOK_ENABLED)
         RxJavaPlugins.getInstance().registerObservableExecutionHook(new DTTimberDebugHook());
   }
}
