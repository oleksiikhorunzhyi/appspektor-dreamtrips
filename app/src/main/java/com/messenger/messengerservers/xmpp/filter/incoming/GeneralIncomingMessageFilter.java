package com.messenger.messengerservers.xmpp.filter.incoming;

import com.messenger.messengerservers.xmpp.util.JidCreatorHelper;
import com.messenger.messengerservers.xmpp.util.XmppPacketDetector;

import org.jivesoftware.smack.packet.Message;

import rx.Observable;

public class GeneralIncomingMessageFilter extends BaseIncomingMessageFilter {

    @Override
    public Observable<Boolean> skipMessage(int type, Message message) {
        return Observable.just(isMessageIgnored(type, message));
    }

    private boolean isMessageIgnored(int packetType, Message message) {
        if (packetType == XmppPacketDetector.EXTENTION_AVATAR) {
            return false;
        }
        boolean ownMessage = message.getType() == Message.Type.groupchat
                && JidCreatorHelper.obtainId(message.getTo()).equals(JidCreatorHelper
                .obtainUserIdFromGroupJid(message.getFrom()));
        boolean delayed = message.getExtension("urn:xmpp:delay") != null;
        return ownMessage || delayed;
    }
}
