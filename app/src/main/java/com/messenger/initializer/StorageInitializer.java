package com.messenger.initializer;

import android.content.Context;

import com.raizlabs.android.dbflow.config.FlowLog;
import com.raizlabs.android.dbflow.config.FlowManager;
import com.worldventures.core.di.AppInitializer;
import com.worldventures.core.janet.Injector;

public class StorageInitializer implements AppInitializer {

   Context context;

   public StorageInitializer(Context context) {
      this.context = context;
   }

   @Override
   public void initialize(Injector injector) {
      FlowManager.init(context);
      FlowLog.setMinimumLoggingLevel(FlowLog.Level.V);
   }
}
