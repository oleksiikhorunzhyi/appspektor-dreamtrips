package com.techery.spares.service;

import android.app.IntentService;

import com.techery.spares.module.Injector;

import dagger.ObjectGraph;

public abstract class InjectingIntentService extends IntentService implements Injector {

   private ObjectGraph objectGraph;

   public InjectingIntentService(String name) {
      super(name);
   }

   @Override
   public ObjectGraph getObjectGraph() {
      return this.objectGraph;
   }

   @Override
   public void inject(Object target) {
      getObjectGraph().inject(target);
   }

   @Override
   public void onCreate() {
      super.onCreate();

      objectGraph = ((Injector) getApplication()).getObjectGraph();

      getObjectGraph().inject(this);
   }
}
