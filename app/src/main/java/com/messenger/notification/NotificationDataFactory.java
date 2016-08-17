package com.messenger.notification;

import android.text.TextUtils;

import com.messenger.entities.DataAttachment;
import com.messenger.entities.DataConversation;
import com.messenger.entities.DataMessage;
import com.messenger.entities.DataUser;
import com.messenger.messengerservers.constant.ConversationStatus;
import com.messenger.notification.model.ImmutableGroupNotificationData;
import com.messenger.notification.model.ImmutableSingleChatNotificationData;
import com.messenger.notification.model.NotificationData;
import com.messenger.storage.dao.AttachmentDAO;
import com.messenger.storage.dao.ConversationsDAO;
import com.messenger.storage.dao.ParticipantsDAO;
import com.messenger.storage.dao.UsersDAO;
import com.messenger.ui.helper.ConversationHelper;

import java.util.List;

import javax.inject.Inject;

import rx.Observable;

public class NotificationDataFactory {
   private final ConversationsDAO conversationsDAO;
   private final ParticipantsDAO participantsDAO;
   private final AttachmentDAO attachmentDAO;
   private final UsersDAO usersDAO;

   @Inject
   NotificationDataFactory(ConversationsDAO conversationsDAO, ParticipantsDAO participantsDAO, AttachmentDAO attachmentDAO, UsersDAO usersDAO) {
      this.conversationsDAO = conversationsDAO;
      this.participantsDAO = participantsDAO;
      this.attachmentDAO = attachmentDAO;
      this.usersDAO = usersDAO;
   }

   public Observable<NotificationData> createNotificationData(DataMessage message) {
      return conversationsDAO.getConversation(message.getConversationId())
            .take(1)
            .filter(this::filterConversation)
            .flatMap(conversation -> createNotificationData(conversation, message));
   }

   private Observable<NotificationData> createNotificationData(DataConversation conversation, DataMessage message) {
      if (ConversationHelper.isSingleChat(conversation)) {
         return Observable.zip(usersDAO.getUserById(message.getFromId())
               .take(1), attachmentDAO.getAttachmentByMessageId(message.getId())
               .take(1), (sender, attachment) -> createChatNotification(sender, message, attachment, conversation));
      } else {
         return Observable.zip(usersDAO.getUserById(message.getFromId())
               .take(1), attachmentDAO.getAttachmentByMessageId(message.getId())
               .take(1), participantsDAO.getParticipantsEntities(message.getConversationId())
               .take(1), (sender, attachment, participants) -> createGroupNotification(sender, message, attachment, conversation, participants));
      }

   }

   private NotificationData createChatNotification(DataUser sender, DataMessage message, DataAttachment attachment, DataConversation conversation) {
      return ImmutableSingleChatNotificationData.builder()
            .sender(sender)
            .message(message)
            .attachment(attachment)
            .conversation(conversation)
            .build();
   }

   private NotificationData createGroupNotification(DataUser sender, DataMessage message, DataAttachment attachment, DataConversation conversation, List<DataUser> participants) {
      return ImmutableGroupNotificationData.builder()
            .participants(participants)
            .sender(sender)
            .message(message)
            .attachment(attachment)
            .conversation(conversation)
            .build();
   }

   private boolean filterConversation(DataConversation conversation) {
      return conversation != null && TextUtils.equals(conversation.getStatus(), ConversationStatus.PRESENT);
   }
}
