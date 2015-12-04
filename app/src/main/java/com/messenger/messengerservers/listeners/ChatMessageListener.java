package com.messenger.messengerservers.listeners;

import com.messenger.messengerservers.entities.User;

public interface ChatMessageListener {

    void receivedMessage(String message, User user);

}
