package com.messenger.messengerservers.xmpp.filter.incoming;

import com.messenger.messengerservers.xmpp.util.XmppPacketDetector;

import org.jivesoftware.smack.packet.Message;
import org.jivesoftware.smack.packet.Stanza;

import rx.Observable;

public abstract class BaseIncomingMessageFilter implements IncomingMessageFilter {

    @Override
    public Observable<Boolean> skipPacket(Stanza stanza) {
        int type = XmppPacketDetector.stanzaType(stanza);
        Message message = (Message) stanza;
        return skipMessage(type, message);
    }

    abstract Observable<Boolean> skipMessage(int type, Message message);
}
