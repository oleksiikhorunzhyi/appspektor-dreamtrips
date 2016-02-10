package com.messenger.messengerservers.listeners;

public interface OnChatJoinedListener {
    void onChatJoined(String conversationId, String userId, boolean isOnline);
}