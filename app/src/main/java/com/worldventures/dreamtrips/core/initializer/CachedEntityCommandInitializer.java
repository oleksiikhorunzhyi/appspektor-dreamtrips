package com.worldventures.dreamtrips.core.initializer;

import com.techery.spares.application.AppInitializer;
import com.techery.spares.module.Injector;
import com.worldventures.dreamtrips.modules.common.command.ResetCachedEntitiesInProgressCommand;
import com.worldventures.dreamtrips.modules.common.delegate.CachedEntityInteractor;

public class CachedEntityCommandInitializer implements AppInitializer {

   private CachedEntityInteractor interactor;

   public CachedEntityCommandInitializer(CachedEntityInteractor interactor) {
      this.interactor = interactor;
   }

   @Override
   public void initialize(Injector injector) {
      interactor.getResetCachedEntitiesInProgressPipe().send(new ResetCachedEntitiesInProgressCommand());
   }
}
