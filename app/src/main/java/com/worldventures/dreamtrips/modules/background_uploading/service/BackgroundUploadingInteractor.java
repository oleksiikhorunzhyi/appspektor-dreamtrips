package com.worldventures.dreamtrips.modules.background_uploading.service;

import com.worldventures.dreamtrips.core.janet.SessionActionPipeCreator;
import com.worldventures.dreamtrips.modules.background_uploading.model.CompoundOperationState;

import java.util.concurrent.TimeUnit;

import io.techery.janet.ActionPipe;
import rx.Observable;
import rx.schedulers.Schedulers;

public class BackgroundUploadingInteractor {

   private static final int DELAY_TO_DELETE_COMPOUND_OPERATION = 2;

   private ActionPipe<CompoundOperationsCommand> compoundOperationsPipe;
   private ActionPipe<PostProcessingCommand> postProcessingPipe;
   private ActionPipe<ScheduleCompoundOperationCommand> scheduleOperationPipe;
   private ActionPipe<StartNextCompoundOperationCommand> startNextCompoundPipe;
   private ActionPipe<PauseCompoundOperationCommand> pauseCompoundOperationPipe;
   private ActionPipe<ResumeCompoundOperationCommand> resumeCompoundOperationPipe;
   private ActionPipe<CancelCompoundOperationCommand> cancelCompoundOperationPipe;

   public BackgroundUploadingInteractor(SessionActionPipeCreator sessionActionPipeCreator) {
      postProcessingPipe = sessionActionPipeCreator.createPipe(PostProcessingCommand.class, Schedulers.io());
      compoundOperationsPipe = sessionActionPipeCreator.createPipe(CompoundOperationsCommand.class, Schedulers.io());
      scheduleOperationPipe = sessionActionPipeCreator.createPipe(ScheduleCompoundOperationCommand.class, Schedulers.io());
      startNextCompoundPipe = sessionActionPipeCreator.createPipe(StartNextCompoundOperationCommand.class, Schedulers.io());
      pauseCompoundOperationPipe = sessionActionPipeCreator.createPipe(PauseCompoundOperationCommand.class, Schedulers.io());
      resumeCompoundOperationPipe = sessionActionPipeCreator.createPipe(ResumeCompoundOperationCommand.class, Schedulers
            .io());
      cancelCompoundOperationPipe = sessionActionPipeCreator.createPipe(CancelCompoundOperationCommand.class, Schedulers
            .io());
      subscribeToPostProcessed();
   }

   public ActionPipe<ScheduleCompoundOperationCommand> scheduleOperationPipe() {
      return scheduleOperationPipe;
   }

   public ActionPipe<CompoundOperationsCommand> compoundOperationsPipe() {
      return compoundOperationsPipe;
   }

   public ActionPipe<PostProcessingCommand> postProcessingPipe() {
      return postProcessingPipe;
   }

   public ActionPipe<StartNextCompoundOperationCommand> startNextCompoundPipe() {
      return startNextCompoundPipe;
   }

   public ActionPipe<PauseCompoundOperationCommand> pauseCompoundOperationPipe() {
      return pauseCompoundOperationPipe;
   }

   public ActionPipe<ResumeCompoundOperationCommand> resumeCompoundOperationPipe() {
      return resumeCompoundOperationPipe;
   }

   public ActionPipe<CancelCompoundOperationCommand> cancelCompoundOperationPipe() {
      return cancelCompoundOperationPipe;
   }

   private void subscribeToPostProcessed() {
      postProcessingPipe.observeSuccess()
            .filter(command -> command.getResult().state() == CompoundOperationState.FINISHED)
            .flatMap(command -> Observable.timer(DELAY_TO_DELETE_COMPOUND_OPERATION, TimeUnit.SECONDS)
                  .map(time -> command.getResult()))
            .subscribe(postCompoundOperationModel -> {
               compoundOperationsPipe.send(CompoundOperationsCommand.compoundCommandRemoved(postCompoundOperationModel));
               startNextCompoundPipe.send(new StartNextCompoundOperationCommand());
            });
   }
}
