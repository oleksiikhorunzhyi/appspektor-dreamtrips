package com.worldventures.core.di;

import android.support.multidex.MultiDexApplication;

import com.appspector.sdk.AppSpector;
import com.worldventures.core.BuildConfig;
import com.worldventures.core.janet.Injector;

import java.util.Set;

import javax.inject.Inject;

import dagger.ObjectGraph;

public abstract class BaseApplicationWithInjector extends MultiDexApplication implements Injector {

   private Injector injector;

   @Inject protected Set<AppInitializer> appInitializers;

   @Override
   public void onCreate() {
      super.onCreate();

      AppSpector
            .build(this)
            .addPerformanceMonitor()
            .addHttpMonitor()
            .addLogMonitor()
            .addScreenshotMonitor()
            .addSQLMonitor()
            .run(BuildConfig.APPSPECTOR_API_KEY);

      injector = createInjector();
      injector.inject(this);

      runInitializers();
   }

   protected abstract Injector createInjector();

   protected void runInitializers() {
      for (AppInitializer initializer : appInitializers) {
         initializer.initialize(this);
      }
   }

   @Override
   public ObjectGraph getObjectGraph() {
      return injector.getObjectGraph();
   }

   @Override
   public void inject(Object target) {
      injector.inject(target);
   }
}
