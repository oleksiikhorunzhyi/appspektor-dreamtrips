package com.messenger.messengerservers.chat;

import java.util.ArrayList;

import com.messenger.messengerservers.ChatState;
import com.messenger.messengerservers.ConnectionException;
import com.messenger.messengerservers.entities.Message;
import com.messenger.messengerservers.entities.User;
import com.messenger.messengerservers.listeners.ChatMessageListener;
import com.messenger.messengerservers.listeners.OnChatStateChangedListener;

public abstract class Chat {
    private final ArrayList<ChatMessageListener> chatMessageListeners = new ArrayList<>();
    private final ArrayList<OnChatStateChangedListener> onChatStateChangedListeners = new ArrayList<>();

    public abstract void sendMessage(Message message) throws ConnectionException;
    public abstract void setCurrentState(ChatState state);

    public void addOnChatMessageListener(ChatMessageListener listener) {
        chatMessageListeners.add(listener);
    }

    public void removeOnChatMessageListener(ChatMessageListener listener) {
        chatMessageListeners.remove(listener);
    }

    public void addOnChatStateListener(OnChatStateChangedListener listener) {
        onChatStateChangedListeners.add(listener);
    }

    public void removeOnChatStateListener(OnChatStateChangedListener listener) {
        onChatStateChangedListeners.remove(listener);
    }

    protected void handleReceiveMessage(Message message, User user) {
        for (ChatMessageListener l: chatMessageListeners) {
            l.receivedMessage(message, user);
        }
    }

    protected void handleChangeState(ChatState state) {
        for (OnChatStateChangedListener l: onChatStateChangedListeners) {
            l.onChatStateChanged(state);
        }
    }

    public void close() {

    }
}
