package com.messenger.delegate.chat;

import android.support.annotation.NonNull;

import com.messenger.entities.DataConversation;
import com.messenger.entities.DataUser;
import com.messenger.messengerservers.MessengerServerFacade;
import com.messenger.messengerservers.chat.Chat;
import com.messenger.messengerservers.chat.ChatManager;
import com.messenger.messengerservers.constant.ConversationType;
import com.messenger.storage.dao.ConversationsDAO;

import java.util.List;

import javax.inject.Inject;

import rx.Observable;

public class CreateChatHelper {

   private final ChatManager chatManager;
   private final ConversationsDAO conversationsDAO;

   @Inject
   CreateChatHelper(MessengerServerFacade messengerServerFacade, ConversationsDAO conversationsDAO) {
      this.chatManager = messengerServerFacade.getChatManager();
      this.conversationsDAO = conversationsDAO;
   }

   public Observable<Chat> createChat(String conversationId) {
      return conversationsDAO.getConversationWithParticipants(conversationId)
            .take(1)
            .flatMap(pair -> createChat(pair.first, pair.second));
   }

   //TODO privatize this method after ChatDelegate refactor
   public Observable<Chat> createChat(DataConversation conversation, @NonNull List<DataUser> participants) {
      switch (conversation.getType()) {
         case ConversationType.CHAT:
            return provideSingleChat(conversation.getId(), participants);
         case ConversationType.GROUP:
         default:
            return provideGroupChat(conversation);
      }
   }


   private Observable<Chat> provideSingleChat(String conversationId, List<DataUser> participants) {
      return Observable.just(participants)
            .filter(usersList -> !usersList.isEmpty())
            .map(users -> users.get(0))
            .map(mate -> chatManager.createSingleUserChat(mate.getId(), conversationId));
   }

   private Observable<Chat> provideGroupChat(DataConversation conversation) {
      return Observable.defer(() -> Observable.just(chatManager.createGroupChat(conversation.getId(), conversation.getOwnerId())));
   }
}
