package com.messenger.messengerservers.xmpp.util;

import android.text.TextUtils;

import com.messenger.messengerservers.xmpp.packets.ChatStateExtension;

import org.jivesoftware.smack.packet.Message;
import org.jivesoftware.smack.packet.Stanza;

public final class XmppPacketDetector {
    public static final int MESSAGE = 0x74747;
    public static final int SUBJECT = 0x74741;
    public static final int SYSTEM = 0x74748;
    public static final int EXTENTION_STATUS = 0x74749;
    public static final int UNKNOW = -1;

    public static int stanzaType(Stanza stanza) {
        if (!(stanza instanceof Message))
            return UNKNOW;

        Message message = (Message) stanza;
        Message.Type messageType = message.getType();
        if (messageType != Message.Type.chat && messageType != Message.Type.groupchat && messageType == Message.Type.normal) {
            return SYSTEM;
        }

        // TODO: 12/23/15 refactoring this
        if (!TextUtils.isEmpty(message.getBody())) {
            return MESSAGE;
        } else if (!TextUtils.isEmpty(message.getSubject())) {
            return SUBJECT;
        } else if (message.getExtension(ChatStateExtension.NAMESPACE) != null) {
            return EXTENTION_STATUS;
        }
        return UNKNOW;
    }
}
