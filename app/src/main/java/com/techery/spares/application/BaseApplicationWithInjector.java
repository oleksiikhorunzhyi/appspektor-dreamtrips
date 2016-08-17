package com.techery.spares.application;

import android.support.multidex.MultiDexApplication;

import com.techery.spares.module.Injector;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import javax.inject.Inject;

import dagger.ObjectGraph;

public abstract class BaseApplicationWithInjector extends MultiDexApplication implements Injector {
   private ObjectGraph objectGraph;

   @Inject protected Set<AppInitializer> appInitializers;

   @Override
   public void onCreate() {
      super.onCreate();

      this.objectGraph = ObjectGraph.create(getModules().toArray());
      inject(this);

      runInitializers();
   }

   protected void runInitializers() {
      for (AppInitializer initializer : appInitializers) {
         initializer.initialize(this);
      }
   }

   protected List<Object> getModules() {
      List<Object> result = new ArrayList<>();
      result.add(getApplicationModule());
      return result;
   }

   protected abstract Object getApplicationModule();

   @Override
   public ObjectGraph getObjectGraph() {
      return objectGraph;
   }

   @Override
   public void inject(Object target) {
      getObjectGraph().inject(target);
   }
}
