package com.messenger.messengerservers.chat;

import com.messenger.messengerservers.ChatState;
import com.messenger.messengerservers.ConnectionException;
import com.messenger.messengerservers.entities.Message;
import com.messenger.messengerservers.entities.Status;
import com.messenger.messengerservers.entities.User;
import com.messenger.messengerservers.listeners.ChatMessageListener;
import com.messenger.messengerservers.listeners.OnChatStateChangedListener;

import java.util.ArrayList;


public abstract class Chat {
    @Deprecated
    private final ArrayList<ChatMessageListener> chatMessageListeners = new ArrayList<>();
    private final ArrayList<OnChatStateChangedListener> onChatStateChangedListeners = new ArrayList<>();

    public abstract void sendMessage(Message message) throws ConnectionException;

    public abstract void changeMessageStatus(Message message, @Status.MessageStatus String status);

    public abstract void setCurrentState(ChatState state);

    @Deprecated
    public void addOnChatMessageListener(ChatMessageListener listener) {
        chatMessageListeners.add(listener);
    }

    @Deprecated
    public void removeOnChatMessageListener(ChatMessageListener listener) {
        chatMessageListeners.remove(listener);
    }

    public void addOnChatStateListener(OnChatStateChangedListener listener) {
        onChatStateChangedListeners.add(listener);
    }

    public void removeOnChatStateListener(OnChatStateChangedListener listener) {
        onChatStateChangedListeners.remove(listener);
    }

    @Deprecated
    protected void handleReceiveMessage(String message, User user) {
        for (ChatMessageListener l : chatMessageListeners) {
            l.receivedMessage(message, user);
        }
    }

    protected void handleChangeState(ChatState state, String userId) {
        for (OnChatStateChangedListener l : onChatStateChangedListeners) {
            l.onChatStateChanged(state, userId);
        }
    }

    public void close() {
        chatMessageListeners.clear();
        onChatStateChangedListeners.clear();
    }
}
