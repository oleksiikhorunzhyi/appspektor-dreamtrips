package com.messenger.messengerservers.xmpp;

import com.messenger.messengerservers.model.Message;

public interface UnhandledMessageListener {

    void onNewUnhandledMessage(Message message);
}
