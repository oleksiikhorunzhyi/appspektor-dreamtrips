package com.worldventures.dreamtrips.social.ui.background_uploading.service;

import com.worldventures.core.janet.SessionActionPipeCreator;
import com.worldventures.dreamtrips.social.ui.background_uploading.service.command.CancelCompoundOperationCommand;
import com.worldventures.dreamtrips.social.ui.background_uploading.service.command.PauseCompoundOperationCommand;
import com.worldventures.dreamtrips.social.ui.background_uploading.service.command.PostProcessingCommand;
import com.worldventures.dreamtrips.social.ui.background_uploading.service.command.RestoreCompoundOperationsCommand;
import com.worldventures.dreamtrips.social.ui.background_uploading.service.command.ResumeCompoundOperationCommand;
import com.worldventures.dreamtrips.social.ui.background_uploading.service.command.ScheduleCompoundOperationCommand;
import com.worldventures.dreamtrips.social.ui.background_uploading.service.command.StartNextCompoundOperationCommand;

import io.techery.janet.ActionPipe;
import rx.schedulers.Schedulers;

public class BackgroundUploadingInteractor {

   private final ActionPipe<PostProcessingCommand> postProcessingPipe;
   private final ActionPipe<ScheduleCompoundOperationCommand> scheduleOperationPipe;
   private final ActionPipe<StartNextCompoundOperationCommand> startNextCompoundPipe;
   private final ActionPipe<PauseCompoundOperationCommand> pauseCompoundOperationPipe;
   private final ActionPipe<RestoreCompoundOperationsCommand> restoreCompoundOperationsPipe;
   private final ActionPipe<ResumeCompoundOperationCommand> resumeCompoundOperationPipe;
   private final ActionPipe<CancelCompoundOperationCommand> cancelCompoundOperationPipe;
   private final ActionPipe<CancelAllCompoundOperationsCommand> cancelAllCompoundOperationsPipe;

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
      cancelAllCompoundOperationsPipe = sessionActionPipeCreator.createPipe(CancelAllCompoundOperationsCommand.class, Schedulers
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

   public ActionPipe<CancelAllCompoundOperationsCommand> cancelAllCompoundOperationsPipe() {
      return cancelAllCompoundOperationsPipe;
   }
}
