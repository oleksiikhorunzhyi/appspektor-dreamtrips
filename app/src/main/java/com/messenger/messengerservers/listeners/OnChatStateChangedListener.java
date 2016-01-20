package com.messenger.messengerservers.listeners;

public interface OnChatStateChangedListener {
    void onChatStateChanged(String conversationId, String userId, String state);
}
