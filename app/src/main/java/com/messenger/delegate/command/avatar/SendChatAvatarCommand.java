package com.messenger.delegate.command.avatar;

import com.messenger.delegate.command.BaseChatCommand;
import com.messenger.entities.DataConversation;
import com.messenger.messengerservers.chat.GroupChat;
import com.messenger.storage.dao.ConversationsDAO;

import javax.inject.Inject;

import io.techery.janet.command.annotations.CommandAction;

@CommandAction
public class SendChatAvatarCommand extends BaseChatCommand<DataConversation> {

   @Inject ConversationsDAO conversationsDAO;

   private final String url;

   public SendChatAvatarCommand(String conversationId, String url) {
      super(conversationId);
      this.url = url;
   }

   protected void run(CommandCallback<DataConversation> callback) {
      getChat().map(chat -> (GroupChat) chat)
            .flatMap(multiUserChat -> multiUserChat.setAvatar(url))
            .flatMap(chat -> conversationsDAO.getConversation(conversationId))
            .take(1)
            .map(conversation -> {
               conversation.setAvatar(url);
               conversationsDAO.save(conversation);
               return conversation;
            })
            .subscribe(dataConversation -> {
               callback.onProgress(100);
               callback.onSuccess(dataConversation);
            }, callback::onFail);
   }
}
