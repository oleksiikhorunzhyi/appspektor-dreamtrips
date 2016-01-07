package com.messenger.messengerservers.listeners;

import com.messenger.messengerservers.entities.Message;

public interface GlobalMessageListener {

    void onReceiveMessage(Message message);

    void onSendMessage(Message message);

    class SimpleGlobalMessageListener implements GlobalMessageListener {

        @Override
        public void onReceiveMessage(Message message) {
        }

        @Override
        public void onSendMessage(Message message) {
        }
    }
}
