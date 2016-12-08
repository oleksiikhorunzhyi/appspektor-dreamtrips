package com.worldventures.dreamtrips.modules.background_uploading.service;

import com.worldventures.dreamtrips.core.janet.SessionActionPipeCreator;

import io.techery.janet.ActionPipe;
import rx.schedulers.Schedulers;

public class BackgroundUploadingInteractor {

   private ActionPipe<CompoundOperationsCommand> compoundOperationsPipe;

   public BackgroundUploadingInteractor(SessionActionPipeCreator sessionActionPipeCreator) {
      compoundOperationsPipe = sessionActionPipeCreator.createPipe(CompoundOperationsCommand.class, Schedulers.io());
   }

   public ActionPipe<CompoundOperationsCommand> compoundOperationsPipe() {
      return compoundOperationsPipe;
   }
}
