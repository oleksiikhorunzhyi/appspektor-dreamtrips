package com.messenger.messengerservers.xmpp;

import com.messenger.messengerservers.GlobalEventEmitter;
import com.messenger.messengerservers.entities.Conversation;
import com.messenger.messengerservers.listeners.AuthorizeListener;
import com.messenger.messengerservers.xmpp.util.JidCreatorHelper;
import com.messenger.messengerservers.xmpp.util.XmppMessageConverter;

import org.jivesoftware.smack.AbstractXMPPConnection;
import org.jivesoftware.smack.filter.StanzaTypeFilter;
import org.jivesoftware.smack.packet.Message;
import org.jivesoftware.smack.packet.Presence;
import org.jivesoftware.smack.packet.Presence.Type;
import org.jivesoftware.smack.packet.Stanza;
import org.jivesoftware.smackx.pubsub.Affiliation;

import static com.messenger.messengerservers.xmpp.util.XmppPacketDetector.MESSAGE;
import static com.messenger.messengerservers.xmpp.util.XmppPacketDetector.SUBJECT;
import static com.messenger.messengerservers.xmpp.util.XmppPacketDetector.isMessage;
import static com.messenger.messengerservers.xmpp.util.XmppPacketDetector.stanzaType;

public class XmppGlobalEventEmitter extends GlobalEventEmitter {

    private final XmppServerFacade facade;
    private AbstractXMPPConnection abstractXMPPConnection;
    private final XmppMessageConverter messageConverter;


    public XmppGlobalEventEmitter(XmppServerFacade facade) {
        this.facade = facade;
        this.messageConverter = new XmppMessageConverter();
        facade.addAuthorizationListener(authorizeListener);
    }

    AuthorizeListener authorizeListener = new AuthorizeListener() {
        @Override
        public void onSuccess() {
            super.onSuccess();
            abstractXMPPConnection = facade.getConnection();
            abstractXMPPConnection.addPacketInterceptor(XmppGlobalEventEmitter.this::interceptOutgoingPacket, null);
            abstractXMPPConnection.addAsyncStanzaListener(XmppGlobalEventEmitter.this::interceptIncomingPacket, null);
            abstractXMPPConnection.addAsyncStanzaListener(XmppGlobalEventEmitter.this::interceptIncomingPresence, StanzaTypeFilter.PRESENCE);
        }
    };

    private void interceptOutgoingPacket(Stanza packet) {
        if (isMessage(packet)) {
            //// TODO: 12/17/15 add from, cause this is a bug: stanza remove FROM from packet 
            com.messenger.messengerservers.entities.Message message = messageConverter.convert((Message) packet);
            message.setFromId(facade.getOwner().getId());
            notifyGlobalMessage(message, false);
        }
    }

    private void interceptIncomingPacket(Stanza packet) {
        if (!facade.isActive()) return;
        //
        switch (stanzaType(packet)) {
            case MESSAGE: {
                Message messageXMPP = (Message) packet;
                if (messageXMPP.getType() == Message.Type.groupchat
                        && messageXMPP.getFrom().split("/")[1].equals(messageXMPP.getTo().split("@")[0])) {
                    return;
                }
                com.messenger.messengerservers.entities.Message message = messageConverter.convert(messageXMPP);
                notifyGlobalMessage(message, true);

                if (!isHandled(message)) {
                    notifyNewUnhandledMessage(message);
                }
                break;
            }
            case SUBJECT:
                notifyOnSubjectChanges(JidCreatorHelper.obtainId(packet.getFrom()), ((Message) packet).getSubject());
                break;
        }
    }

    private void interceptIncomingPresence(Stanza stanza) {
        Presence presence = (Presence) stanza;
        Type presenceType = presence.getType();
        if (presenceType == null) return;

        String fromJid = stanza.getFrom();
        if (JidCreatorHelper.isGroupJid(fromJid)) {
            notifyOnLeftChatListener(JidCreatorHelper.obtainGroupJid(fromJid),
                    JidCreatorHelper.obtainUserIdFromGroupJid(fromJid));
        } else {
            if (Type.available == presenceType && Type.unavailable == presenceType) {
                notifyUserPresenceChanged(JidCreatorHelper.obtainId(presence.getFrom()), Type.available == presenceType);
            }
        }
    }

    private boolean isHandled(com.messenger.messengerservers.entities.Message message) {
        for (Conversation conversation : handledConversations) {
            if (conversation.getId().equals(message.getConversationId())) {
                return true;
            }
        }

        return false;
    }

}
