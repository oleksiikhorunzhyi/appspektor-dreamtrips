package com.messenger.messengerservers.xmpp;

import org.jivesoftware.smack.AbstractXMPPConnection;
import org.jivesoftware.smack.XMPPConnection;
import org.jivesoftware.smack.packet.Message;
import org.jivesoftware.smack.packet.Stanza;
import org.jivesoftware.smackx.muc.MultiUserChat;
import org.jivesoftware.smackx.muc.MultiUserChatManager;

import com.messenger.messengerservers.GlobalEventEmitter;
import com.messenger.messengerservers.entities.Conversation;
import com.messenger.messengerservers.entities.User;
import com.messenger.messengerservers.listeners.AuthorizeListener;
import com.messenger.messengerservers.xmpp.util.JidCreatorHelper;
import com.messenger.messengerservers.xmpp.util.XmppMessageConverter;

import static com.messenger.messengerservers.xmpp.util.XmppPacketDetector.isMessage;

public class XmppGlobalEventEmitter extends GlobalEventEmitter {
    private final XmppServerFacade facade;

    private AbstractXMPPConnection abstractXMPPConnection;
    private final AuthorizeListener authorizeListener = new AuthorizeListener() {
        @Override
        public void onSuccess() {
            super.onSuccess();
            abstractXMPPConnection = facade.getConnection();
            abstractXMPPConnection.addPacketInterceptor(XmppGlobalEventEmitter.this::interceptOutgoingPacket, null);
            abstractXMPPConnection.addAsyncStanzaListener(XmppGlobalEventEmitter.this::interceptIncomingPacket, null);
            MultiUserChatManager.getInstanceFor(abstractXMPPConnection).addInvitationListener(XmppGlobalEventEmitter.this::receiveInvite);
        }
    };
    
    public XmppGlobalEventEmitter(XmppServerFacade facade) {
        facade.addAuthorizationListener(authorizeListener);
        this.facade = facade;
    }

    private void interceptOutgoingPacket(Stanza packet){
        if(isMessage(packet)){
            notifyGlobalMessage(XmppMessageConverter.convert((Message) packet), false);
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
        User userInviter = JidCreatorHelper.obtainUser(inviter);
        notifyReceiveInvite(userInviter, room.getRoom(), password);
    }

}
