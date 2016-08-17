package com.worldventures.dreamtrips.core.initializer;

import android.content.Context;

import com.techery.spares.application.AppInitializer;
import com.techery.spares.module.Injector;

import net.danlew.android.joda.JodaTimeAndroid;

public class JodaTimeInitializer implements AppInitializer {

   private Context context;

   public JodaTimeInitializer(Context context) {
      this.context = context;
   }

   @Override
   public void initialize(Injector injector) {
      JodaTimeAndroid.init(context);
   }
}