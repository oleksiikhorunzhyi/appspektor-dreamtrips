package com.worldventures.dreamtrips.social.initializer;

import com.worldventures.core.di.AppInitializer;
import com.worldventures.core.janet.Injector;
import com.worldventures.core.modules.video.service.command.ResetCachedModelsInProgressCommand;
import com.worldventures.core.service.CachedEntityInteractor;
import com.worldventures.core.service.command.MigrateFromCachedEntityCommand;

public class CachedEntityCommandInitializer implements AppInitializer {

   private CachedEntityInteractor interactor;

   public CachedEntityCommandInitializer(CachedEntityInteractor interactor) {
      this.interactor = interactor;
   }

   @Override
   public void initialize(Injector injector) {
      interactor.getMigrateFromCachedEntityPipe()
            .createObservableResult(new MigrateFromCachedEntityCommand())
            .subscribe(command -> interactor.getResetCachedModelsInProgressPipe()
                  .send(new ResetCachedModelsInProgressCommand()));
   }
}
