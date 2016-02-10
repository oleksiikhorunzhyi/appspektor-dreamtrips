package com.messenger.messengerservers.listeners;

public interface OnChatCreatedListener {

    void onChatCreated(String conversationId, boolean createLocally);
}
