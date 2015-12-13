package com.messenger.messengerservers.listeners;

import com.messenger.messengerservers.entities.Message;

public interface GlobalMessageListener {
    void onReceiveMessage(Message message);

    void onSendMessage(Message message);
}
