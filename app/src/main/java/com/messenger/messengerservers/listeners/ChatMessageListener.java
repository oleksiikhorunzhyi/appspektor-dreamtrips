package com.messenger.messengerservers.listeners;

import com.messenger.messengerservers.entities.Message;
import com.messenger.messengerservers.entities.User;

public interface ChatMessageListener {

    void receivedMessage(Message message, User user);

}
