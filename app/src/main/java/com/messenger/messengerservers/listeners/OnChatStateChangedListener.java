package com.messenger.messengerservers.listeners;

import com.messenger.messengerservers.ChatState;

public interface OnChatStateChangedListener {
    void onChatStateChanged(ChatState state, String userId);
}
