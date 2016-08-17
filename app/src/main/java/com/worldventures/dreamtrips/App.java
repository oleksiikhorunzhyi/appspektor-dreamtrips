package com.worldventures.dreamtrips;

import android.content.Context;
import android.support.multidex.MultiDex;

import com.techery.spares.application.BaseApplicationWithInjector;
import com.worldventures.dreamtrips.core.module.AppModule;

public class App extends BaseApplicationWithInjector {

   @Override
   protected Object getApplicationModule() {
      return new AppModule(this);
   }

   @Override
   protected void attachBaseContext(Context base) {
      super.attachBaseContext(base);
      MultiDex.install(this);
   }
}