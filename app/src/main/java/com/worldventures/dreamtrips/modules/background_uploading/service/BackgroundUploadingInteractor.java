package com.worldventures.dreamtrips.modules.background_uploading.service;

import com.techery.spares.session.SessionHolder;
import com.worldventures.dreamtrips.core.janet.SessionActionPipeCreator;
import com.worldventures.dreamtrips.core.session.UserSession;
import com.worldventures.dreamtrips.modules.background_uploading.model.CompoundOperationState;
import com.worldventures.dreamtrips.modules.feed.event.FeedItemAddedEvent;
import com.worldventures.dreamtrips.modules.feed.model.FeedItem;

import java.util.concurrent.TimeUnit;

import de.greenrobot.event.EventBus;
import io.techery.janet.ActionPipe;
import rx.Observable;
import rx.schedulers.Schedulers;

public class BackgroundUploadingInteractor {

   private static final int DELAY_TO_DELETE_COMPOUND_OPERATION = 3;

   private ActionPipe<CompoundOperationsCommand> compoundOperationsPipe;
   private ActionPipe<PostProcessingCommand> postProcessingPipe;
   private ActionPipe<ScheduleCompoundOperationCommand> scheduleOperationPipe;
   private ActionPipe<StartNextCompoundOperationCommand> startNextCompoundPipe;
   private ActionPipe<PauseCompoundOperationCommand> pauseCompoundOperationPipe;
   private ActionPipe<QueryCompoundOperationsCommand> queryCompoundOperationsPipe;
   private ActionPipe<RestoreCompoundOperationsCommand> restoreCompoundOperationsPipe;
   private ActionPipe<ResumeCompoundOperationCommand> resumeCompoundOperationPipe;
   private ActionPipe<CancelCompoundOperationCommand> cancelCompoundOperationPipe;

   private EventBus eventBus;
   private SessionHolder<UserSession> sessionHolder;

   public BackgroundUploadingInteractor(SessionActionPipeCreator sessionActionPipeCreator,EventBus eventBus,
         SessionHolder<UserSession> sessionHolder) {
      this.eventBus = eventBus;
      this.sessionHolder = sessionHolder;
      postProcessingPipe = sessionActionPipeCreator.createPipe(PostProcessingCommand.class, Schedulers.io());
      compoundOperationsPipe = sessionActionPipeCreator.createPipe(CompoundOperationsCommand.class, Schedulers.io());
      queryCompoundOperationsPipe = sessionActionPipeCreator.createPipe(QueryCompoundOperationsCommand.class, Schedulers.io());
      scheduleOperationPipe = sessionActionPipeCreator.createPipe(ScheduleCompoundOperationCommand.class, Schedulers.io());
      startNextCompoundPipe = sessionActionPipeCreator.createPipe(StartNextCompoundOperationCommand.class, Schedulers.io());
      restoreCompoundOperationsPipe = sessionActionPipeCreator.createPipe(RestoreCompoundOperationsCommand.class, Schedulers.io());
      pauseCompoundOperationPipe = sessionActionPipeCreator.createPipe(PauseCompoundOperationCommand.class, Schedulers.io());
      resumeCompoundOperationPipe = sessionActionPipeCreator.createPipe(ResumeCompoundOperationCommand.class, Schedulers
            .io());
      cancelCompoundOperationPipe = sessionActionPipeCreator.createPipe(CancelCompoundOperationCommand.class, Schedulers
            .io());
      subscribeToPostProcessed();
   }

   public ActionPipe<RestoreCompoundOperationsCommand> restoreCompoundOperationsPipe() {
      return restoreCompoundOperationsPipe;
   }

   public ActionPipe<QueryCompoundOperationsCommand> queryCompoundOperationsPipe() {
      return queryCompoundOperationsPipe;
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
               eventBus.post(new FeedItemAddedEvent(FeedItem.create(postCompoundOperationModel.body().createdPost(),
                     sessionHolder.get().get().getUser())));
               compoundOperationsPipe.send(CompoundOperationsCommand.compoundCommandRemoved(postCompoundOperationModel));
               startNextCompoundPipe.send(new StartNextCompoundOperationCommand());
            });
   }
}
