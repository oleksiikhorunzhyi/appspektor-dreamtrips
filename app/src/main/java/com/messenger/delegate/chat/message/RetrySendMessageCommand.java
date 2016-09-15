package com.messenger.delegate.chat.message;

import com.messenger.delegate.MessageBodyCreator;
import com.messenger.delegate.command.BaseChatCommand;
import com.messenger.entities.DataMessage;
import com.messenger.messengerservers.model.Message;
import com.messenger.storage.dao.MessageDAO;
import com.messenger.util.ChatDateUtils;

import javax.inject.Inject;

import io.techery.janet.command.annotations.CommandAction;

@CommandAction
public class RetrySendMessageCommand extends BaseChatCommand<Message> {

   @Inject MessageBodyCreator messageBodyCreator;
   @Inject MessageDAO messageDAO;

   private DataMessage failedMessage;

   public RetrySendMessageCommand(DataMessage failedMessage) {
      super(failedMessage.getConversationId());
      this.failedMessage = failedMessage;
   }

   @Override
   protected void run(CommandCallback<Message> callback) throws Throwable {
      Message message = failedMessage.toChatMessage();
      message.setMessageBody(messageBodyCreator.provideForText(failedMessage.getText()));
      getChat().flatMap(chat -> chat.send(message)).subscribe(callback::onSuccess, throwable -> {
         long time = ChatDateUtils.getErrorMessageDate();
         messageDAO.updateStatus(message.getId(), message.getStatus(), time);
         conversationsDAO.updateDate(message.getConversationId(), time);

         callback.onFail(throwable);
      });
   }
}
