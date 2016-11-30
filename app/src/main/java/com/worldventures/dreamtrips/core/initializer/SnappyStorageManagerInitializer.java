package com.worldventures.dreamtrips.core.initializer;

import com.techery.spares.application.AppInitializer;
import com.techery.spares.module.Injector;
import com.worldventures.dreamtrips.wallet.domain.storage.disk.SnappyStorageManager;

import javax.inject.Inject;

public class SnappyStorageManagerInitializer implements AppInitializer {

   @Inject SnappyStorageManager storageManager;

   @Override
   public void initialize(Injector injector) {
      injector.inject(this);

      storageManager.init();
   }
}
