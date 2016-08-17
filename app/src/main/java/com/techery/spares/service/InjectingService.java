package com.techery.spares.service;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

import com.techery.spares.module.InjectingServiceModule;
import com.techery.spares.module.Injector;
import com.techery.spares.utils.ModuleHelper;

import java.util.ArrayList;
import java.util.List;

import dagger.ObjectGraph;

public abstract class InjectingService extends Service implements Injector {

   public static final String EXTRA_PAYLOAD = "com.techery.spares.service.extra.PAYLOAD";

   protected ServiceActionRouter actionRouter = new ServiceActionRouter();

   private ObjectGraph objectGraph;

   @Override
   public IBinder onBind(Intent intent) {
      return null;
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

      inject(this);
   }

   @Override
   public int onStartCommand(Intent intent, int flags, int startId) {

      actionRouter.dispatchIntent(intent);

      return Service.START_STICKY;
   }

   protected List<Object> getModules() {
      List<Object> result = new ArrayList<>();

      result.add(new InjectingServiceModule(this, this));

      Object usedModule = ModuleHelper.getUsedModule(this);

      if (usedModule != null) {
         result.add(usedModule);
      }

      return result;
   }
}
