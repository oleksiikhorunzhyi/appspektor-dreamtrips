package com.messenger.messengerservers.xmpp.chats;

import org.jivesoftware.smack.SmackException;

interface SendAction<T extends org.jivesoftware.smack.packet.Stanza> {
    boolean call(T message) throws SmackException.NotConnectedException;
}