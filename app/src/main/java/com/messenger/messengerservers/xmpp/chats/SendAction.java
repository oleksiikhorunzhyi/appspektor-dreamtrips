package com.messenger.messengerservers.xmpp.chats;

import com.messenger.messengerservers.ConnectionException;

import org.jivesoftware.smack.SmackException;

interface SendAction<T extends org.jivesoftware.smack.packet.Stanza> {
    void call(T message) throws  SmackException.NotConnectedException, ConnectionException;
}