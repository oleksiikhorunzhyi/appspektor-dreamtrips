package com.worldventures.dreamtrips.qa;

import javax.inject.Named;

import dagger.Module;
import dagger.Provides;

@Module(library = true)
public class DefaultQaConfigModule {

   public static final String LABEL = "DefaultQaConfigModule";

   @Provides
   @Named(LABEL)
   QaAppConfig provideQaAppConfig() {
      return new QaAppConfig(true, true);
   }
}
