package com.worldventures.dreamtrips.core.repository;

import android.content.Context;

import com.techery.spares.module.qualifier.ForApplication;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;

@Module(complete = false, library = true)
public class SnappyModule {

   @Provides
   @Singleton
   SnappyRepository snappyRepositoryImpl(@ForApplication Context appContext, DefaultSnappyOpenHelper defaultSnappyOpenHelper) {
      return new SnappyRepositoryImpl(appContext, defaultSnappyOpenHelper);
   }

   @Provides
   @Singleton
   DefaultSnappyOpenHelper defaultSnappyOpenHelper() {
      return new DefaultSnappyOpenHelper();
   }
}