package com.messenger.messengerservers.listeners;

public interface OnChatLeftListener {

    void onChatLeft(String conversationId, String userId);
}