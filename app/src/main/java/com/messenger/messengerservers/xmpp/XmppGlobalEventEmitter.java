package com.messenger.messengerservers.xmpp;

import com.messenger.messengerservers.GlobalEventEmitter;
import com.messenger.messengerservers.listeners.AuthorizeListener;
import com.messenger.messengerservers.xmpp.util.JidCreatorHelper;
import com.messenger.messengerservers.xmpp.util.XmppMessageConverter;

import org.jivesoftware.smack.AbstractXMPPConnection;
import org.jivesoftware.smack.chat.Chat;
import org.jivesoftware.smack.chat.ChatManager;
import org.jivesoftware.smack.filter.StanzaTypeFilter;
import org.jivesoftware.smack.packet.Message;
import org.jivesoftware.smack.packet.Presence;
import org.jivesoftware.smack.packet.Presence.Type;
import org.jivesoftware.smack.packet.Stanza;
import org.jivesoftware.smackx.muc.MUCRole;
import org.jivesoftware.smackx.muc.packet.MUCUser;

import static com.messenger.messengerservers.xmpp.util.XmppPacketDetector.MESSAGE;
import static com.messenger.messengerservers.xmpp.util.XmppPacketDetector.SUBJECT;
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
            abstractXMPPConnection.addPacketInterceptor(XmppGlobalEventEmitter.this::interceptOutgoingPacket, StanzaTypeFilter.MESSAGE);
            abstractXMPPConnection.addAsyncStanzaListener(XmppGlobalEventEmitter.this::interceptIncomingMessage, StanzaTypeFilter.MESSAGE);
            abstractXMPPConnection.addAsyncStanzaListener(XmppGlobalEventEmitter.this::interceptIncomingPresence, StanzaTypeFilter.PRESENCE);
            ChatManager.getInstanceFor(abstractXMPPConnection).addChatListener(XmppGlobalEventEmitter.this::onChatCreated);
        }
    };

    private void interceptOutgoingPacket(Stanza packet) {
    }

    private void onChatCreated(Chat chat, boolean createdLocally) {
        notifyOnChatCreatedListener(chat.getThreadID(), createdLocally);
    }

    public void interceptOutgoingMessages(com.messenger.messengerservers.entities.Message message) {
        // TODO: 1/7/16 set fromId in chat
        message.setFromId(facade.getOwner().getId());
        notifyGlobalMessage(message, false);
    }

    private void interceptIncomingMessage(Stanza packet) {
        if (!facade.isActive()) return;

        Message messageXMPP = (Message) packet;
        if (isMessageIgnored(messageXMPP)) return;
        switch (stanzaType(packet)) {
            case MESSAGE:
                com.messenger.messengerservers.entities.Message message = messageConverter.convert(messageXMPP);
                notifyGlobalMessage(message, true);
                notifyNewUnhandledMessage(message); // TODO remove unhandled listeners
                break;
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
        boolean processed = processParticipantsActions(presence, fromJid);
        if (!processed && (Type.available == presenceType || Type.unavailable == presenceType)) {
            notifyUserPresenceChanged(JidCreatorHelper.obtainUserIdFromGroupJid(fromJid), Type.available == presenceType);
        }
    }

    private boolean processParticipantsActions(Presence presence, String fromJid) {
        MUCUser extension = (MUCUser) presence.getExtension(MUCUser.NAMESPACE);
        if (extension == null || extension.getStatus().isEmpty() || !JidCreatorHelper.isGroupJid(fromJid))
            return false;
        //
        String conversationId = JidCreatorHelper.obtainId(fromJid);
        String jid = extension.getItem().getJid();
        String userId = jid == null ?
                JidCreatorHelper.obtainUserIdFromGroupJid(fromJid) : JidCreatorHelper.obtainId(jid);
        //
        MUCRole role = extension.getItem().getRole();
        if (role == null || extension.getStatus().isEmpty()) return false;
        //
        if (role == MUCRole.none) {
            notifyOnChatLeftListener(conversationId, userId);
        } else {
            notifyOnChatJoinedListener(conversationId, userId);
        }
        return true;
    }

    private boolean isMessageIgnored(Message message) {
        boolean ownMessage = message.getType() == Message.Type.groupchat
                && JidCreatorHelper.obtainId(message.getTo()).equals(JidCreatorHelper.obtainUserIdFromGroupJid(message.getFrom()));
        boolean delayed = message.getExtension("urn:xmpp:delay") != null;
        return ownMessage || delayed;
    }

}
