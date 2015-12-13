package com.messenger.messengerservers.xmpp;

import com.messenger.messengerservers.entities.Message;

public interface UnhandledMessageListener {

    void onNewUnhandledMessage(Message message);
}
