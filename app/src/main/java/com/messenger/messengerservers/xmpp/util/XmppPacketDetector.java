package com.messenger.messengerservers.xmpp.util;

import org.jivesoftware.smack.packet.Message;
import org.jivesoftware.smack.packet.Stanza;

public final class XmppPacketDetector {

    public static boolean isMessage(Stanza stanza) {
        if (!(stanza instanceof Message))
            return false;

        Message message = (Message) stanza;
        Message.Type messageType = message.getType();
        if (messageType != Message.Type.chat && messageType != Message.Type.groupchat)
            return false;

        return message.getBody() != null;
    }
}
