package com.techery.spares.service;

import android.app.IntentService;
import android.content.Intent;

import com.techery.spares.module.InjectingServiceModule;
import com.techery.spares.module.Injector;
import com.techery.spares.utils.ModuleHelper;

import java.util.ArrayList;
import java.util.List;

import dagger.ObjectGraph;

public abstract class InjectingIntentService extends IntentService implements Injector {

   protected ServiceActionRouter actionRouter = new ServiceActionRouter();

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

      objectGraph = ((Injector) getApplication()).getObjectGraph().plus(getModules().toArray());

      getObjectGraph().inject(this);
   }

   @Override
   protected void onHandleIntent(Intent intent) {
      actionRouter.dispatchIntent(intent);
   }

   protected List<Object> getModules() {
      List<Object> result = new ArrayList<Object>();

      result.add(new InjectingServiceModule(this, this));
      Object usedModule = ModuleHelper.getUsedModule(this);

      if (usedModule != null) {
         result.add(usedModule);
      }

      return result;
   }
}
