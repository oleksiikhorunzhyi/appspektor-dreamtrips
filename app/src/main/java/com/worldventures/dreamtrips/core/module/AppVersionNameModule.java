package com.worldventures.dreamtrips.core.module;


import com.worldventures.core.utils.AppVersionNameBuilder;
import com.worldventures.dreamtrips.BuildConfig;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;

@Module(library = true,
        complete = false)

public class AppVersionNameModule {

   @Provides
   @Singleton
   AppVersionNameBuilder provideAppVersionNameBuilder() {
      return new AppVersionNameBuilder(BuildConfig.versionMajor, BuildConfig.versionMinor,
            BuildConfig.versionPatch, BuildConfig.versionBuild,
            BuildConfig.FLAVOR, BuildConfig.BUILD_TYPE);
   }
}
