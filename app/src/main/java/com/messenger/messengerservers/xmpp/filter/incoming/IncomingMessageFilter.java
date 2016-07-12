package com.messenger.messengerservers.xmpp.filter.incoming;

import org.jivesoftware.smack.packet.Stanza;

import rx.Observable;

public interface IncomingMessageFilter {

    Observable<Boolean> skipPacket(Stanza stanza);
}
