package com.messenger.messengerservers.xmpp.util;

import org.jivesoftware.smack.packet.Message;
import org.jivesoftware.smack.packet.Stanza;

public final class XmppPacketDetector {
    public static final int MESSAGE = 0x74747;
    public static final int SUBJECT = 0x74741;
    public static final int UNKNOW = -1;

    @Deprecated
    public static boolean isMessage(Stanza stanza) {
        return MESSAGE == stanzaType(stanza);
    }

    public static int stanzaType(Stanza stanza) {
        if (!(stanza instanceof Message))
            return UNKNOW;

        Message message = (Message) stanza;
        Message.Type messageType = message.getType();
        if (messageType != Message.Type.chat && messageType != Message.Type.groupchat)
            return UNKNOW;

        // TODO: 12/23/15 refactoring this
        if (message.getBody() != null) {
            return MESSAGE;
        } if (message.getSubject() != null) {
            return SUBJECT;
        }
        return UNKNOW;
    }
}
