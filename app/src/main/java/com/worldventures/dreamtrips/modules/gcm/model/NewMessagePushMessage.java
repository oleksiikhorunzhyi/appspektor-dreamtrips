package com.worldventures.dreamtrips.modules.gcm.model;

public class NewMessagePushMessage extends PushMessage {

    public final String conversationId;

    public NewMessagePushMessage(AlertWrapper alertWrapper, PushType type, int notificationId, int notificationsCount, int requestsCount, String conversationId) {
        super(alertWrapper, type, notificationId, notificationsCount, requestsCount);
        this.conversationId = conversationId;
    }

}
