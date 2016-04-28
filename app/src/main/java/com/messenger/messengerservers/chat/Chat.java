package com.messenger.messengerservers.chat;

import com.messenger.messengerservers.ChatState;
import com.messenger.messengerservers.model.Message;

import rx.Observable;

public interface Chat {
    Observable<Message> send(Message message);

    Observable<String> sendReadStatus(String messageId);

    Observable<String> setCurrentState(@ChatState.State String state);

    void close();
}
