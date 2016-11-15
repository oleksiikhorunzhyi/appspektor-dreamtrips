package com.worldventures.dreamtrips.modules.infopages.service;


import com.worldventures.dreamtrips.core.janet.SessionActionPipeCreator;
import com.worldventures.dreamtrips.modules.infopages.service.command.GetFeedbackCommand;
import com.worldventures.dreamtrips.modules.infopages.service.command.SendFeedbackCommand;

import io.techery.janet.ActionPipe;
import rx.schedulers.Schedulers;

public class FeedbackInteractor {

   private final ActionPipe<SendFeedbackCommand> sendFeedbackPipe;
   private final ActionPipe<GetFeedbackCommand> getFeeedbackPipe;

   public FeedbackInteractor(SessionActionPipeCreator sessionActionPipeCreator) {
      sendFeedbackPipe = sessionActionPipeCreator.createPipe(SendFeedbackCommand.class, Schedulers.io());
      getFeeedbackPipe = sessionActionPipeCreator.createPipe(GetFeedbackCommand.class, Schedulers.io());
   }

   public ActionPipe<GetFeedbackCommand> getFeeedbackPipe() {
      return getFeeedbackPipe;
   }

   public ActionPipe<SendFeedbackCommand> sendFeedbackPipe() {
      return sendFeedbackPipe;
   }
}
