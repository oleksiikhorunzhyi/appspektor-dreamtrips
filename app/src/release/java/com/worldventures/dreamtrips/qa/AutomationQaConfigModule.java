package com.worldventures.dreamtrips.qa;

import javax.inject.Named;
import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;

@Module(complete = false, library = true, includes = DefaultQaConfigModule.class)
public class AutomationQaConfigModule {

   @Provides
   @Singleton
   QaConfig provideQaConfig(@Named(DefaultQaConfigModule.LABEL) QaAppConfig defaultAppConfig) {
      return new QaConfig(null, defaultAppConfig);
   }

}
