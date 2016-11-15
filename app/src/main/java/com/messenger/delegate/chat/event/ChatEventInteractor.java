package com.messenger.delegate.chat.event;

import com.worldventures.dreamtrips.core.janet.SessionActionPipeCreator;

import javax.inject.Inject;
import javax.inject.Singleton;

import io.techery.janet.ActionPipe;
import rx.schedulers.Schedulers;

@Singleton
public class ChatEventInteractor {
   private final ActionPipe<ClearChatCommand> eventClearChatPipe;
   private final ActionPipe<RevertClearingChatCommand> eventRevertClearingChatPipe;

   @Inject
   ChatEventInteractor(SessionActionPipeCreator sessionActionPipeCreator) {
      eventClearChatPipe = sessionActionPipeCreator.createPipe(ClearChatCommand.class, Schedulers.io());
      eventRevertClearingChatPipe = sessionActionPipeCreator.createPipe(RevertClearingChatCommand.class, Schedulers.io());
   }

   public ActionPipe<ClearChatCommand> getEventClearChatPipe() {
      return eventClearChatPipe;
   }

   public ActionPipe<RevertClearingChatCommand> getEventRevertClearingChatPipe() {
      return eventRevertClearingChatPipe;
   }
}
