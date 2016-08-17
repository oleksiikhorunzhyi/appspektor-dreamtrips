package com.messenger.delegate;


import com.messenger.entities.DataAttachment;
import com.messenger.entities.DataLocationAttachment;
import com.messenger.entities.DataMessage;
import com.messenger.entities.DataPhotoAttachment;
import com.messenger.messengerservers.constant.AttachmentType;
import com.messenger.messengerservers.constant.MessageStatus;
import com.messenger.messengerservers.constant.MessageType;

import java.util.Date;
import java.util.UUID;

public class AttachmentDelegateHelper {

   public DataMessage createEmptyMessage(String userId, String conversationId) {
      return new DataMessage.Builder().conversationId(conversationId)
            .from(userId)
            .id(UUID.randomUUID().toString())
            .date(new Date(System.currentTimeMillis()))
            .status(MessageStatus.SENDING)
            .syncTime(System.currentTimeMillis())
            .type(MessageType.MESSAGE)
            .build();
   }

   public DataAttachment createDataAttachment(DataMessage dataMessage, @AttachmentType.Type String type) {
      return new DataAttachment.Builder().conversationId(dataMessage.getConversationId())
            .messageId(dataMessage.getId())
            .type(type)
            .build();
   }

   public DataPhotoAttachment createEmptyPhotoAttachment(DataAttachment attachment) {
      return new DataPhotoAttachment.Builder().id(attachment.getId())
            .state(DataPhotoAttachment.PhotoAttachmentStatus.NONE)
            .build();
   }

   public DataLocationAttachment createLocationAttachment(DataAttachment attachment, double lat, double lng) {
      return new DataLocationAttachment.Builder().id(attachment.getId()).coordinates(lat, lng).build();
   }
}
