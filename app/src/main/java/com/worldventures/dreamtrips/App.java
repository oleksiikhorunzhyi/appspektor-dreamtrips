package com.worldventures.dreamtrips;

import com.worldventures.core.di.BaseApplicationWithInjector;
import com.worldventures.dreamtrips.core.module.AppModule;

public class App extends BaseApplicationWithInjector {

   @Override
   protected Object getApplicationModule() {
      return new AppModule(this);
   }

}