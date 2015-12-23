package com.messenger.messengerservers.xmpp;

import com.messenger.messengerservers.GlobalEventEmitter;
import com.messenger.messengerservers.entities.Conversation;
import com.messenger.messengerservers.entities.User;
import com.messenger.messengerservers.listeners.AuthorizeListener;
import com.messenger.messengerservers.xmpp.util.JidCreatorHelper;
import com.messenger.messengerservers.xmpp.util.XmppMessageConverter;

import org.jivesoftware.smack.AbstractXMPPConnection;
import org.jivesoftware.smack.SmackException;
import org.jivesoftware.smack.XMPPConnection;
import org.jivesoftware.smack.XMPPException;
import org.jivesoftware.smack.packet.Message;
import org.jivesoftware.smack.packet.Presence;
import org.jivesoftware.smack.packet.Stanza;
import org.jivesoftware.smack.roster.Roster;
import org.jivesoftware.smack.roster.RosterListener;
import org.jivesoftware.smackx.muc.MultiUserChat;
import org.jivesoftware.smackx.muc.MultiUserChatManager;

import java.util.Collection;

import timber.log.Timber;

import static com.messenger.messengerservers.xmpp.util.XmppPacketDetector.MESSAGE;
import static com.messenger.messengerservers.xmpp.util.XmppPacketDetector.SUBJECT;
import static com.messenger.messengerservers.xmpp.util.XmppPacketDetector.isMessage;
import static com.messenger.messengerservers.xmpp.util.XmppPacketDetector.stanzaType;

public class XmppGlobalEventEmitter extends GlobalEventEmitter {

    private final XmppServerFacade facade;
    private AbstractXMPPConnection abstractXMPPConnection;
    private final XmppMessageConverter messageConverter;

    public final RosterListener rosterListener = new RosterListener() {
        @Override
        public void entriesAdded(Collection<String> addresses) {
        }

        @Override
        public void entriesUpdated(Collection<String> addresses) {
        }

        @Override
        public void entriesDeleted(Collection<String> addresses) {
        }

        @Override
        public void presenceChanged(Presence presence) {
            Presence.Type presenceType = presence.getType();
            if (presenceType != Presence.Type.available && presenceType != Presence.Type.unavailable)
                return;

            String jid = presence.getFrom();
            User user = new User(jid.substring(0, jid.indexOf("@")));
            user.setOnline(presenceType.equals(Presence.Type.available));
            notifyUserPresenceChanged(user);
        }
    };

    private final AuthorizeListener authorizeListener = new AuthorizeListener() {
        @Override
        public void onSuccess() {
            super.onSuccess();
            abstractXMPPConnection = facade.getConnection();
            abstractXMPPConnection.addPacketInterceptor(XmppGlobalEventEmitter.this::interceptOutgoingPacket, null);
            abstractXMPPConnection.addAsyncStanzaListener(XmppGlobalEventEmitter.this::interceptIncomingPacket, null);
            MultiUserChatManager.getInstanceFor(abstractXMPPConnection).addInvitationListener(XmppGlobalEventEmitter.this::receiveInvite);
            Roster.getInstanceFor(facade.getConnection()).addRosterListener(rosterListener);
        }
    };

    public XmppGlobalEventEmitter(XmppServerFacade facade) {
        this.facade = facade;
        this.messageConverter = new XmppMessageConverter();
        facade.addAuthorizationListener(authorizeListener);
    }

    private void interceptOutgoingPacket(Stanza packet) {
        if (isMessage(packet)) {
            //// TODO: 12/17/15 add from, cause this is a bug: stanza remove FROM from packet 
            com.messenger.messengerservers.entities.Message message = messageConverter.convert((Message) packet);
            message.setFromId(facade.getOwner().getId());
            notifyGlobalMessage(message, false);
        }
    }

    private void interceptIncomingPacket(Stanza packet) {
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

    private boolean isHandled(com.messenger.messengerservers.entities.Message message) {
        for (Conversation conversation : handledConversations) {
            if (conversation.getId().equals(message.getConversationId())) {
                return true;
            }
        }

        return false;
    }

    private void receiveInvite(XMPPConnection conn, MultiUserChat room, String inviter,
                               String reason, String password, Message message) {
        try {
            room.join(JidCreatorHelper.obtainId(abstractXMPPConnection.getUser()));
        } catch (SmackException | XMPPException.XMPPErrorException e) {
            Timber.e(e, "Can't join :(");
        }
    }

}
