package com.messenger.messengerservers.listeners;

import com.messenger.messengerservers.ChatState;

public interface OnChatStateChangedListener {
    void onChatStateChanged(String conversationId, String userId, @ChatState.State String state);
}
