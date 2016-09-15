package com.messenger.messengerservers.listeners;

import com.messenger.messengerservers.chat.ChatState;

public interface OnChatStateChangedListener {
   void onChatStateChanged(String conversationId, String userId, @ChatState.State String state);
}
