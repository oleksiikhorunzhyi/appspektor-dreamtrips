package com.messenger.messengerservers.xmpp.chats;

import android.support.annotation.NonNull;

import org.jivesoftware.smack.AbstractXMPPConnection;

public interface ConnectionClient {
    void setConnection(@NonNull AbstractXMPPConnection connection);
}
