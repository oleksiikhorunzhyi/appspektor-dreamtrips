package com.worldventures.dreamtrips.modules.gcm.model;

public class NewUnsupportedMessage extends NewMessagePushMessage {

    public NewUnsupportedMessage(AlertWrapper alertWrapper, PushType type, int notificationId, int notificationsCount, int requestsCount, String conversationId, int unreadConversationsCount) {
        super(alertWrapper, type, notificationId, notificationsCount, requestsCount, conversationId, unreadConversationsCount);
    }
}
