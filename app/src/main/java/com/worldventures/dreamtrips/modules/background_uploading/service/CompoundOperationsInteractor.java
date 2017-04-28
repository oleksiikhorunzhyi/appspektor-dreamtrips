package com.worldventures.dreamtrips.modules.background_uploading.service;

import com.worldventures.dreamtrips.core.janet.SessionActionPipeCreator;
import com.worldventures.dreamtrips.modules.background_uploading.service.command.CompoundOperationsCommand;

import java.util.concurrent.Executors;

import io.techery.janet.ActionPipe;
import rx.Scheduler;
import rx.schedulers.Schedulers;

public class CompoundOperationsInteractor {

   private ActionPipe<CompoundOperationsCommand> compoundOperationsPipe;

   public CompoundOperationsInteractor(SessionActionPipeCreator sessionActionPipeCreator, Scheduler scheduler) {
      compoundOperationsPipe = sessionActionPipeCreator.createPipe(CompoundOperationsCommand.class, scheduler);
   }

   public ActionPipe<CompoundOperationsCommand> compoundOperationsPipe() {
      return compoundOperationsPipe;
   }
}
