package com.worldventures.dreamtrips.core.initializer;

import android.app.Application;

import com.newrelic.agent.android.NewRelic;
import com.worldventures.core.di.AppInitializer;
import com.worldventures.core.janet.Injector;
import com.worldventures.dreamtrips.BuildConfig;

import javax.inject.Inject;

public class NewrelicInitializer implements AppInitializer {

   @Inject protected Application application;

   @Override
   public void initialize(Injector injector) {
      if (BuildConfig.DEBUG || !BuildConfig.NEWRELIC_ENABLED) return;
      //
      injector.inject(this);
      NewRelic.withApplicationToken(BuildConfig.NEWRELIC_API_KEY).start(application);
   }
}
