package com.messenger.messengerservers.chat;

import com.messenger.messengerservers.ChatState;
import com.messenger.messengerservers.model.Message;

import rx.Observable;

public abstract class Chat {
    public abstract Observable<Message> send(Message message);

    public abstract Observable<String> sendReadStatus(String messageId);

    public abstract void setCurrentState(@ChatState.State String state);

    public void close() {
    }
}
