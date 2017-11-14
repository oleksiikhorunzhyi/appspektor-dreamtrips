package com.worldventures.dreamtrips.core.initializer;

import android.content.Context;

import com.worldventures.core.di.AppInitializer;
import com.worldventures.core.janet.Injector;

import net.danlew.android.joda.JodaTimeAndroid;

public class JodaTimeInitializer implements AppInitializer {

   private final Context context;

   public JodaTimeInitializer(Context context) {
      this.context = context;
   }

   @Override
   public void initialize(Injector injector) {
      JodaTimeAndroid.init(context);
   }
}
