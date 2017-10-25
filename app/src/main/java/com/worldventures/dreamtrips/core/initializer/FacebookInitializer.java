package com.worldventures.dreamtrips.core.initializer;

import android.content.Context;

import com.facebook.FacebookSdk;
import com.worldventures.core.di.AppInitializer;
import com.worldventures.core.janet.Injector;

import javax.inject.Inject;

public class FacebookInitializer implements AppInitializer {

   @Inject protected Context context;

   @Override
   public void initialize(Injector injector) {
      injector.inject(this);
      FacebookSdk.sdkInitialize(context);
   }
}
