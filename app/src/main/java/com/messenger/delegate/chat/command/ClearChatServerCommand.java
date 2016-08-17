package com.messenger.delegate.chat.command;

import com.messenger.messengerservers.ChatExtensions;
import com.messenger.messengerservers.event.ClearChatEvent;
import com.worldventures.dreamtrips.core.janet.dagger.InjectableAction;

import javax.inject.Inject;

import io.techery.janet.Command;
import io.techery.janet.command.annotations.CommandAction;

@CommandAction
public class ClearChatServerCommand extends Command<ClearChatEvent> implements InjectableAction {

   @Inject ChatExtensions chatExtensions;

   private final String conversationId;

   public ClearChatServerCommand(String conversationId) {
      this.conversationId = conversationId;
   }

   @Override
   protected void run(CommandCallback<ClearChatEvent> callback) throws Throwable {
      chatExtensions.clearChat(conversationId, System.currentTimeMillis())
            .subscribe(callback::onSuccess, callback::onFail);
   }
}
