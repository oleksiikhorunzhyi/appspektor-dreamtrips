package com.worldventures.dreamtrips.core.initializer;

import com.techery.spares.application.AppInitializer;
import com.techery.spares.module.Injector;
import com.worldventures.dreamtrips.modules.version_check.service.VersionCheckInteractor;

public class VersionCheckInitializer implements AppInitializer {
   private VersionCheckInteractor versionCheckInteractor;

   public VersionCheckInitializer(VersionCheckInteractor versionCheckInteractor) {
      this.versionCheckInteractor = versionCheckInteractor;
   }

   @Override
   public void initialize(Injector injector) {
   //   versionCheckInteractor.versionCheckPipe().send(new VersionCheckCommand());
   }
}
