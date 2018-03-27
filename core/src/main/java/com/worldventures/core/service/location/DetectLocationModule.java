package com.worldventures.core.service.location;

import android.content.Context;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;

@Module(complete = false, library = true)
public class DetectLocationModule {

   @Singleton
   @Provides
   DetectLocationService detectLocationService(Context appContext) {
      return new AndroidDetectLocationService(appContext);
   }
}
