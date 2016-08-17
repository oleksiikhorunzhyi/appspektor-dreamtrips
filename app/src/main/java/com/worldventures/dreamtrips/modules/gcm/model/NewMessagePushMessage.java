package com.worldventures.dreamtrips.modules.gcm.model;

public class NewMessagePushMessage extends PushMessage {

   public final String conversationId;
   public final int unreadConversationsCount;

   public NewMessagePushMessage(AlertWrapper alertWrapper, PushType type, int notificationId, int notificationsCount, int requestsCount, String conversationId, int unreadConversationsCount) {
      super(alertWrapper, type, notificationId, notificationsCount, requestsCount);
      this.conversationId = conversationId;
      this.unreadConversationsCount = unreadConversationsCount;
   }

}
