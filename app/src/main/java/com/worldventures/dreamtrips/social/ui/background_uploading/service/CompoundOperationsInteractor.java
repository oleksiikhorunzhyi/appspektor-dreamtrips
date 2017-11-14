package com.worldventures.dreamtrips.social.ui.background_uploading.service;

import com.worldventures.core.janet.SessionActionPipeCreator;
import com.worldventures.dreamtrips.social.ui.background_uploading.service.command.CompoundOperationsCommand;

import io.techery.janet.ActionPipe;
import rx.Scheduler;

public class CompoundOperationsInteractor {

   private final ActionPipe<CompoundOperationsCommand> compoundOperationsPipe;

   public CompoundOperationsInteractor(SessionActionPipeCreator sessionActionPipeCreator, Scheduler scheduler) {
      compoundOperationsPipe = sessionActionPipeCreator.createPipe(CompoundOperationsCommand.class, scheduler);
   }

   public ActionPipe<CompoundOperationsCommand> compoundOperationsPipe() {
      return compoundOperationsPipe;
   }
}
