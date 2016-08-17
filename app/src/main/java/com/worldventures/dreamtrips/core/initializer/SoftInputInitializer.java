package com.worldventures.dreamtrips.core.initializer;

import android.app.Activity;
import android.app.Application;

import com.techery.spares.application.AppInitializer;
import com.techery.spares.module.Injector;
import com.techery.spares.utils.SimpleActivityLifecycleCallbacks;
import com.techery.spares.utils.ui.SoftInputUtil;

import javax.inject.Inject;

public class SoftInputInitializer implements AppInitializer {

   @Inject protected Application app;

   @Override
   public void initialize(Injector injector) {
      injector.inject(this);
      //
      app.registerActivityLifecycleCallbacks(new SimpleActivityLifecycleCallbacks() {
         @Override
         public void onActivityStopped(Activity activity) {
            SoftInputUtil.hideSoftInputMethod(activity);
         }
      });
   }
}
