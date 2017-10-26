package com.worldventures.dreamtrips.core.initializer;

import android.app.Application;

import com.crashlytics.android.Crashlytics;
import com.crashlytics.android.core.CrashlyticsCore;
import com.worldventures.core.di.AppInitializer;
import com.worldventures.core.janet.Injector;
import com.worldventures.dreamtrips.BuildConfig;

import javax.inject.Inject;

import io.fabric.sdk.android.Fabric;

public class FabricInitializer implements AppInitializer {

   @Inject protected Application application;

   @Override
   public void initialize(Injector injector) {
      injector.inject(this);
      Crashlytics crashlyticsKit =
            new Crashlytics.Builder().core(new CrashlyticsCore.Builder().disabled(BuildConfig.CRASHLYTICS_DISABLED)
            .build()).build();
      Fabric.with(application, crashlyticsKit);
   }
}
