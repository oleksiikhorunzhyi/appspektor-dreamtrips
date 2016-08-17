package com.messenger.delegate.chat.event;

import com.messenger.messengerservers.event.RevertClearingEvent;
import com.messenger.storage.dao.ConversationsDAO;
import com.worldventures.dreamtrips.core.janet.dagger.InjectableAction;

import javax.inject.Inject;

import io.techery.janet.Command;
import io.techery.janet.command.annotations.CommandAction;

@CommandAction
public class RevertClearingChatCommand extends Command<Void> implements InjectableAction {
   private final RevertClearingEvent revertClearingEvent;

   @Inject ConversationsDAO conversationsDAO;

   public RevertClearingChatCommand(RevertClearingEvent revertClearingEvent) {
      this.revertClearingEvent = revertClearingEvent;
   }

   @Override
   protected void run(CommandCallback<Void> callback) throws Throwable {
      conversationsDAO.setClearDate(revertClearingEvent.getConversationId(), 0);
      callback.onSuccess(null);
   }

   public String getConversationId() {
      return revertClearingEvent.getConversationId();
   }
}
