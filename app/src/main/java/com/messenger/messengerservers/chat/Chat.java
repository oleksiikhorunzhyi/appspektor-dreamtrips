package com.messenger.messengerservers.chat;

import com.messenger.messengerservers.ChatState;
import com.messenger.messengerservers.entities.Message;
import com.messenger.messengerservers.listeners.OnChatStateChangedListener;

import java.util.ArrayList;

import rx.Observable;

public abstract class Chat {
    private final ArrayList<OnChatStateChangedListener> onChatStateChangedListeners = new ArrayList<>();

    public abstract Observable<Message> send(Message message);

    public abstract Observable<Message> sendReadStatus(Message message);

    public abstract void setCurrentState(ChatState state);

    public void addOnChatStateListener(OnChatStateChangedListener listener) {
        onChatStateChangedListeners.add(listener);
    }

    public void removeOnChatStateListener(OnChatStateChangedListener listener) {
        onChatStateChangedListeners.remove(listener);
    }

    protected void handleChangeState(ChatState state, String userId) {
        for (OnChatStateChangedListener l : onChatStateChangedListeners) {
            l.onChatStateChanged(state, userId);
        }
    }

    public void close() {
        onChatStateChangedListeners.clear();
    }
}
