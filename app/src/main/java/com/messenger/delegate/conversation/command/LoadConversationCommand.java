package com.messenger.delegate.conversation.command;

import com.messenger.messengerservers.MessengerServerFacade;
import com.messenger.messengerservers.model.Conversation;
import com.worldventures.dreamtrips.core.janet.dagger.InjectableAction;

import javax.inject.Inject;

import io.techery.janet.Command;
import io.techery.janet.command.annotations.CommandAction;

@CommandAction
public class LoadConversationCommand extends Command<Conversation> implements InjectableAction {

   @Inject MessengerServerFacade messengerServerFacade;

   private final String conversationId;

   public LoadConversationCommand(String conversationId) {
      this.conversationId = conversationId;
   }

   @Override
   protected void run(CommandCallback<Conversation> callback) throws Throwable {
      messengerServerFacade.getLoaderManager()
            .createConversationLoader(conversationId)
            .load()
            .subscribe(callback::onSuccess, callback::onFail);
   }
}
