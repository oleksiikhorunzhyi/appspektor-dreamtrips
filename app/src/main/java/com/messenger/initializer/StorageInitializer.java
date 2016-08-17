package com.messenger.initializer;

import android.content.Context;

import com.raizlabs.android.dbflow.config.FlowLog;
import com.raizlabs.android.dbflow.config.FlowManager;
import com.techery.spares.application.AppInitializer;
import com.techery.spares.module.Injector;

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
