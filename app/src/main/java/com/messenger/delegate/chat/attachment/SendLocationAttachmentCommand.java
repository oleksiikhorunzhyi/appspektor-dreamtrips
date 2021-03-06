package com.messenger.delegate.chat.attachment;

import android.support.annotation.NonNull;

import com.messenger.delegate.MessageBodyCreator;
import com.messenger.delegate.command.BaseChatCommand;
import com.messenger.entities.DataAttachment;
import com.messenger.entities.DataLocationAttachment;
import com.messenger.entities.DataMessage;
import com.messenger.messengerservers.constant.MessageStatus;
import com.messenger.messengerservers.model.AttachmentHolder;
import com.messenger.messengerservers.model.Message;
import com.messenger.storage.dao.AttachmentDAO;
import com.messenger.storage.dao.LocationDAO;
import com.messenger.storage.dao.MessageDAO;

import java.util.Date;

import javax.inject.Inject;

import io.techery.janet.Command;
import io.techery.janet.command.annotations.CommandAction;
import rx.Observable;

@CommandAction
public class SendLocationAttachmentCommand extends BaseChatCommand<DataMessage> {
   private final DataMessage message;
   private final DataAttachment attachment;
   private final DataLocationAttachment locationAttachment;

   @Inject MessageDAO messageDAO;
   @Inject AttachmentDAO attachmentDAO;
   @Inject LocationDAO locationDAO;
   @Inject MessageBodyCreator messageBodyCreator;

   public SendLocationAttachmentCommand(String conversationId, @NonNull DataMessage message, @NonNull DataAttachment attachment, @NonNull DataLocationAttachment locationAttachment) {
      super(conversationId);
      this.message = message;
      this.attachment = attachment;
      this.locationAttachment = locationAttachment;
   }

   @Override
   protected void run(Command.CommandCallback<DataMessage> callback) {
      startSending();
      sendLocationMessage(locationAttachment.getLat(), locationAttachment.getLng()).map(msg -> message)
            .subscribe(message -> callback.onSuccess(message), throwable -> callback.onFail(throwable));
   }

   private void startSending() {
      message.setStatus(MessageStatus.SENDING);

      attachmentDAO.save(attachment);
      locationDAO.save(locationAttachment);
      saveMessage(System.currentTimeMillis());
   }

   private void saveMessage(long time) {
      message.setDate(new Date(time));
      message.setSyncTime(time);
      messageDAO.save(message);
   }

   private Observable<Message> sendLocationMessage(double lat, double lng) {
      Message msg = message.toChatMessage();
      msg.setMessageBody(messageBodyCreator.provideForAttachment(AttachmentHolder.newLocationAttachment(lat, lng)));


      return getChat().flatMap(chat -> chat.send(msg));
   }
}
