package com.messenger.delegate.chat.message;

import com.messenger.delegate.command.BaseChatCommand;
import com.messenger.entities.DataMessage;
import com.messenger.storage.dao.MessageDAO;
import com.techery.spares.session.SessionHolder;
import com.worldventures.dreamtrips.core.session.UserSession;

import javax.inject.Inject;

import io.techery.janet.command.annotations.CommandAction;
import rx.Observable;
import timber.log.Timber;

@CommandAction
public class MarkMessageAsReadCommand extends BaseChatCommand<Boolean> {

   @Inject MessageDAO messageDAO;
   @Inject SessionHolder<UserSession> sessionHolder;

   private DataMessage lastSeenMessage;

   public MarkMessageAsReadCommand(DataMessage lastSeenMessage, String conversationId) {
      super(conversationId);
      this.lastSeenMessage = lastSeenMessage;
   }

   @Override
   protected void run(CommandCallback<Boolean> callback) throws Throwable {
      String currentUserId = sessionHolder.get().get().getUsername();
      long lastSeenMessageTime = lastSeenMessage.getDate().getTime();
      messageDAO.getLastOtherUserMessage(conversationId, currentUserId, lastSeenMessageTime).flatMap(dataMessage -> {
         if (dataMessage == null) {
            return Observable.just(false);
         } else {
            Timber.d("Marking as read " + dataMessage.toString());
            return getChat().flatMap(chat -> chat.sendReadStatus(dataMessage.getId())).flatMap(messageId -> {
               conversationsDAO.setUnreadCount(conversationId, 0);
               return messageDAO.markMessagesAsRead(conversationId, currentUserId, lastSeenMessageTime);
            }).map(messageCount -> true);
         }
      }).subscribe(callback::onSuccess, callback::onFail);
   }
}
