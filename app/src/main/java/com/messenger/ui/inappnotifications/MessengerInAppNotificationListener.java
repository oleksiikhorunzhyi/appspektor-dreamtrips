package com.messenger.ui.inappnotifications;

public class MessengerInAppNotificationListener implements InAppNotificationEventListener {

    private final String conversationId;

    public MessengerInAppNotificationListener(String conversationId) {
        this.conversationId = conversationId;
    }

    @Override
    public void onClick() {
        openConversation(conversationId);
    }

    @Override
    public void onClose() {
        onCloseNotification();
    }

    public void openConversation(String conversationId) {
    }

    public void onCloseNotification() {
    }
}
