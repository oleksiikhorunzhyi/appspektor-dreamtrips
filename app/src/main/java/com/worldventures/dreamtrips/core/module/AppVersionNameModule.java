package com.worldventures.dreamtrips.core.module;

import com.worldventures.dreamtrips.core.utils.AppVersionNameBuilder;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;

@Module(library = true,
        complete = false)

public class AppVersionNameModule {

   @Provides
   @Singleton
   AppVersionNameBuilder provideAppVersionNameBuilder() {
      return new AppVersionNameBuilder();
   }
}
