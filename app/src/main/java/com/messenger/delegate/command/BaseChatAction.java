package com.messenger.delegate.command;

import com.messenger.entities.DataConversation;
import com.messenger.messengerservers.MessengerServerFacade;
import com.messenger.messengerservers.chat.Chat;
import com.messenger.messengerservers.chat.GroupChat;
import com.messenger.ui.helper.ConversationHelper;
import com.worldventures.dreamtrips.core.janet.dagger.InjectableAction;

import javax.inject.Inject;

import io.techery.janet.Command;
import rx.Observable;

public abstract class BaseChatAction<Result> extends Command<Result> implements InjectableAction {
   protected final DataConversation conversation;

   @Inject MessengerServerFacade messengerServerFacade;

   protected BaseChatAction(DataConversation conversation) {
      this.conversation = conversation;
   }

   public DataConversation getConversation() {
      return conversation;
   }

   protected Observable<GroupChat> createMultiChat() {
      return messengerServerFacade.getChatManager()
            .createGroupChatObservable(conversation.getId(), messengerServerFacade.getUsername());
   }

   protected Chat getChat() {
      if (ConversationHelper.isSingleChat(conversation)) {
         return messengerServerFacade.getChatManager().createSingleUserChat(null, conversation.getId());
      } else {
         return messengerServerFacade.getChatManager().createGroupChat(conversation.getId(), conversation.getOwnerId());
      }
   }
}
