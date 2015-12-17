package com.messenger.messengerservers.xmpp;

import android.util.Log;

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

import com.messenger.messengerservers.GlobalEventEmitter;
import com.messenger.messengerservers.entities.Conversation;
import com.messenger.messengerservers.entities.User;
import com.messenger.messengerservers.listeners.AuthorizeListener;
import com.messenger.messengerservers.xmpp.util.JidCreatorHelper;
import com.messenger.messengerservers.xmpp.util.XmppMessageConverter;

import java.util.Collection;

import static com.messenger.messengerservers.xmpp.util.XmppPacketDetector.isMessage;

public class XmppGlobalEventEmitter extends GlobalEventEmitter {
    private static final String TAG = "XmppGlobalEventEmitter";
    private final XmppServerFacade facade;

    private AbstractXMPPConnection abstractXMPPConnection;

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
        facade.addAuthorizationListener(authorizeListener);
        this.facade = facade;
    }

    private void interceptOutgoingPacket(Stanza packet){
        if(isMessage(packet)){
            //// TODO: 12/17/15 add from, cause this is a bug: stanza remove FROM from packet 
            com.messenger.messengerservers.entities.Message message = XmppMessageConverter.convert((Message) packet);
            message.setFrom(facade.getOwner());
            notifyGlobalMessage(message, false);
        }
    }

    private void interceptIncomingPacket(Stanza packet){
        if(isMessage(packet)){
            com.messenger.messengerservers.entities.Message message = XmppMessageConverter.convert((Message) packet);
            notifyGlobalMessage(message, true);

            if (!isHandled(message)){
                notifyNewUnhandledMessage(message);
            }
        }
    }

    private boolean isHandled(com.messenger.messengerservers.entities.Message message){
        for (Conversation conversation: handledConversations){
            if (conversation.getId().equals(message.getConversationId())) {
                return true;
            }
        }

        return false;
    }

    private void receiveInvite(XMPPConnection conn, MultiUserChat room, String inviter,
                                 String reason, String password, Message message){
        try {
            room.join(JidCreatorHelper.obtainUser(abstractXMPPConnection.getUser()).getUserName());
        } catch (SmackException | XMPPException.XMPPErrorException e) {
            Log.e("XmppGlobalEventEmitter", Log.getStackTraceString(e));
        }
//        User userInviter = JidCreatorHelper.obtainUser(inviter);
//        notifyReceiveInvite(userInviter, room.getRoom(), password);
    }

}
