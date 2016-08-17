package com.techery.spares.module;

import android.app.Application;

import com.squareup.leakcanary.LeakCanary;
import com.squareup.leakcanary.RefWatcher;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;

@Module(library = true, complete = false)
public class DebugModule {

   @Provides
   @Singleton
   public RefWatcher provideRefWatcher(Application app) {
      return LeakCanary.install(app);
   }
}
