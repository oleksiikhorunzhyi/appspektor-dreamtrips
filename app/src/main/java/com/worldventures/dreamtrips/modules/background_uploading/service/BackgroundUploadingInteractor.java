package com.worldventures.dreamtrips.modules.background_uploading.service;

import com.worldventures.dreamtrips.core.janet.SessionActionPipeCreator;
import com.worldventures.dreamtrips.modules.background_uploading.service.command.CancelCompoundOperationCommand;
import com.worldventures.dreamtrips.modules.background_uploading.service.command.PauseCompoundOperationCommand;
import com.worldventures.dreamtrips.modules.background_uploading.service.command.PostProcessingCommand;
import com.worldventures.dreamtrips.modules.background_uploading.service.command.RestoreCompoundOperationsCommand;
import com.worldventures.dreamtrips.modules.background_uploading.service.command.ResumeCompoundOperationCommand;
import com.worldventures.dreamtrips.modules.background_uploading.service.command.ScheduleCompoundOperationCommand;
import com.worldventures.dreamtrips.modules.background_uploading.service.command.StartNextCompoundOperationCommand;

import io.techery.janet.ActionPipe;
import rx.schedulers.Schedulers;

public class BackgroundUploadingInteractor {

   private ActionPipe<PostProcessingCommand> postProcessingPipe;
   private ActionPipe<ScheduleCompoundOperationCommand> scheduleOperationPipe;
   private ActionPipe<StartNextCompoundOperationCommand> startNextCompoundPipe;
   private ActionPipe<PauseCompoundOperationCommand> pauseCompoundOperationPipe;
   private ActionPipe<RestoreCompoundOperationsCommand> restoreCompoundOperationsPipe;
   private ActionPipe<ResumeCompoundOperationCommand> resumeCompoundOperationPipe;
   private ActionPipe<CancelCompoundOperationCommand> cancelCompoundOperationPipe;

   public BackgroundUploadingInteractor(SessionActionPipeCreator sessionActionPipeCreator) {
      postProcessingPipe = sessionActionPipeCreator.createPipe(PostProcessingCommand.class, Schedulers.io());
      scheduleOperationPipe = sessionActionPipeCreator.createPipe(ScheduleCompoundOperationCommand.class, Schedulers.io());
      startNextCompoundPipe = sessionActionPipeCreator.createPipe(StartNextCompoundOperationCommand.class, Schedulers.io());
      restoreCompoundOperationsPipe = sessionActionPipeCreator.createPipe(RestoreCompoundOperationsCommand.class, Schedulers
            .io());
      pauseCompoundOperationPipe = sessionActionPipeCreator.createPipe(PauseCompoundOperationCommand.class, Schedulers.io());
      resumeCompoundOperationPipe = sessionActionPipeCreator.createPipe(ResumeCompoundOperationCommand.class, Schedulers
            .io());
      cancelCompoundOperationPipe = sessionActionPipeCreator.createPipe(CancelCompoundOperationCommand.class, Schedulers
            .io());
   }

   public ActionPipe<RestoreCompoundOperationsCommand> restoreCompoundOperationsPipe() {
      return restoreCompoundOperationsPipe;
   }

   public ActionPipe<ScheduleCompoundOperationCommand> scheduleOperationPipe() {
      return scheduleOperationPipe;
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
}
