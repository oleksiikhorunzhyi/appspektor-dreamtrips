package com.messenger.messengerservers.xmpp.util;

import android.text.TextUtils;

import com.messenger.messengerservers.xmpp.extensions.ChangeAvatarExtension;
import com.messenger.messengerservers.xmpp.extensions.ChatStateExtension;
import com.messenger.messengerservers.xmpp.extensions.SystemMessageExtension;

import org.jivesoftware.smack.packet.Message;
import org.jivesoftware.smack.packet.Stanza;

import timber.log.Timber;

public final class XmppPacketDetector {
    public static final int MESSAGE = 0x74747;
    public static final int SUBJECT = 0x74741;
    public static final int EXTENTION_STATUS = 0x74749;
    public static final int EXTENTION_AVATAR = 0x74750;
    public static final int EXTENTION_SYSTEM_MESSAGE = 0x74751;
    public static final int UNKNOWN = -1;

    public static int stanzaType(Stanza stanza) {
        if (!(stanza instanceof Message))
            return UNKNOWN;

        Message message = (Message) stanza;

        if (message.getType() == Message.Type.normal) {
            return UNKNOWN;
        }

        // TODO: 12/23/15 refactoring this
        if (!TextUtils.isEmpty(message.getBody())) {
            return MESSAGE;
        } else if (!TextUtils.isEmpty(message.getSubject())) {
            return SUBJECT;
        } else if (message.getExtension(ChatStateExtension.ELEMENT, ChatStateExtension.NAMESPACE) != null) {
            return EXTENTION_STATUS;
        } else if (message.getExtension(ChangeAvatarExtension.ELEMENT, ChangeAvatarExtension.NAMESPACE) != null) {
            return EXTENTION_AVATAR;
        } else if (message.getExtension(SystemMessageExtension.ELEMENT, SystemMessageExtension.NAMESPACE) != null) {
            return EXTENTION_SYSTEM_MESSAGE;
        }
        return UNKNOWN;
    }
}
