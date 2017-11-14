package com.worldventures.wallet.di.initializer;

import com.worldventures.core.di.AppInitializer;
import com.worldventures.core.janet.Injector;
import com.worldventures.wallet.domain.storage.disk.SnappyStorageManager;

import javax.inject.Inject;

public class SnappyStorageManagerInitializer implements AppInitializer {

   @Inject SnappyStorageManager storageManager;

   @Override
   public void initialize(Injector injector) {
      injector.inject(this);

      storageManager.init();
   }
}
