package com.worldventures.dreamtrips.core.initializer;

import com.techery.spares.application.AppInitializer;
import com.techery.spares.module.Injector;
import com.worldventures.dreamtrips.modules.common.command.ResetCachedModelsInProgressCommand;
import com.worldventures.dreamtrips.modules.common.delegate.CachedEntityInteractor;
import com.worldventures.dreamtrips.modules.video.service.command.MigrateFromCachedEntity;

public class CachedEntityCommandInitializer implements AppInitializer {

   private CachedEntityInteractor interactor;

   public CachedEntityCommandInitializer(CachedEntityInteractor interactor) {
      this.interactor = interactor;
   }

   @Override
   public void initialize(Injector injector) {
      interactor.getMigrateFromCachedEntityPipe()
            .createObservableResult(new MigrateFromCachedEntity())
            .subscribe(command -> interactor.getResetCachedModelsInProgressPipe()
                  .send(new ResetCachedModelsInProgressCommand()));
   }
}
