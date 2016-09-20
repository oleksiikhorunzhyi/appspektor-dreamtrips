package com.messenger.delegate.chat;

import com.messenger.delegate.chat.command.ClearChatServerCommand;
import com.messenger.delegate.chat.command.LoadChatMessagesCommand;
import com.messenger.delegate.chat.command.RevertClearingChatServerCommand;
import com.worldventures.dreamtrips.core.janet.SessionActionPipeCreator;

import javax.inject.Inject;
import javax.inject.Singleton;

import io.techery.janet.ActionPipe;
import rx.schedulers.Schedulers;

@Singleton
public class ChatExtensionInteractor {

   private final ActionPipe<ClearChatServerCommand> clearChatServerCommandActionPipe;
   private final ActionPipe<RevertClearingChatServerCommand> revertClearingChatServerCommandActionPipe;

   private final ActionPipe<LoadChatMessagesCommand> loadChatMessagesCommandActionPipe;

   @Inject
   ChatExtensionInteractor(SessionActionPipeCreator sessionActionPipeCreator) {
      clearChatServerCommandActionPipe = sessionActionPipeCreator.createPipe(ClearChatServerCommand.class,
            Schedulers.io());
      revertClearingChatServerCommandActionPipe = sessionActionPipeCreator.createPipe(RevertClearingChatServerCommand.class,
            Schedulers.io());
      loadChatMessagesCommandActionPipe = sessionActionPipeCreator.createPipe(LoadChatMessagesCommand.class,
            Schedulers.io());
   }

   public ActionPipe<ClearChatServerCommand> getClearChatServerCommandActionPipe() {
      return clearChatServerCommandActionPipe;
   }

   public ActionPipe<RevertClearingChatServerCommand> getRevertClearingChatServerCommandActionPipe() {
      return revertClearingChatServerCommandActionPipe;
   }

   public ActionPipe<LoadChatMessagesCommand> getLoadChatMessagesCommandActionPipe() {
      return loadChatMessagesCommandActionPipe;
   }
}
