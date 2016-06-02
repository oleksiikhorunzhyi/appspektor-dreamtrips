package com.messenger.messengerservers.xmpp;

import android.text.TextUtils;

import com.innahema.collections.query.queriables.Queryable;
import com.messenger.messengerservers.GlobalEventEmitter;
import com.messenger.messengerservers.model.ImmutableParticipant;
import com.messenger.messengerservers.xmpp.extensions.ChangeAvatarExtension;
import com.messenger.messengerservers.xmpp.extensions.ChatStateExtension;
import com.messenger.messengerservers.xmpp.extensions.DeleteMessageExtension;
import com.messenger.messengerservers.xmpp.stanzas.incoming.LeftRoomPresence;
import com.messenger.messengerservers.xmpp.stanzas.incoming.MessageDeletedPresence;
import com.messenger.messengerservers.xmpp.util.JidCreatorHelper;
import com.messenger.messengerservers.xmpp.util.ThreadCreatorHelper;
import com.messenger.messengerservers.xmpp.util.XmppMessageConverter;
import com.messenger.messengerservers.xmpp.util.XmppPacketDetector;

import org.jivesoftware.smack.XMPPConnection;
import org.jivesoftware.smack.filter.StanzaTypeFilter;
import org.jivesoftware.smack.packet.Message;
import org.jivesoftware.smack.packet.Presence;
import org.jivesoftware.smack.packet.Presence.Type;
import org.jivesoftware.smack.packet.Stanza;
import org.jivesoftware.smack.provider.ProviderManager;
import org.jivesoftware.smack.roster.Roster;
import org.jivesoftware.smack.roster.RosterListener;
import org.jivesoftware.smackx.muc.MUCAffiliation;
import org.jivesoftware.smackx.muc.MultiUserChat;
import org.jivesoftware.smackx.muc.MultiUserChatManager;
import org.jivesoftware.smackx.muc.packet.MUCItem;
import org.jivesoftware.smackx.muc.packet.MUCUser;

import java.util.Collection;

import static com.messenger.messengerservers.xmpp.util.XmppPacketDetector.EXTENTION_AVATAR;
import static com.messenger.messengerservers.xmpp.util.XmppPacketDetector.EXTENTION_STATUS;
import static com.messenger.messengerservers.xmpp.util.XmppPacketDetector.MESSAGE;
import static com.messenger.messengerservers.xmpp.util.XmppPacketDetector.SUBJECT;
import static com.messenger.messengerservers.xmpp.util.XmppPacketDetector.stanzaType;

public class XmppGlobalEventEmitter extends GlobalEventEmitter {
    private final XmppServerFacade facade;
    private final XmppMessageConverter messageConverter;


    public XmppGlobalEventEmitter(XmppServerFacade facade) {
        this.facade = facade;
        this.messageConverter = new XmppMessageConverter(facade.getGson());
        facade.getConnectionObservable()
                .subscribe(this::prepareManagers);
    }

    private void prepareManagers(XMPPConnection connection) {
        connection.addAsyncStanzaListener(this::interceptIncomingMessage,
                StanzaTypeFilter.MESSAGE);
        connection.addAsyncStanzaListener(this::interceptIncomingPresence,
                StanzaTypeFilter.PRESENCE);
        connection.addAsyncStanzaListener(this::interceptLeftPresence,
                LeftRoomPresence.LEAVE_PRESENCE_FILTER);
        connection.addAsyncStanzaListener(this::interceptIncomingMessageDeletedPresence,
                MessageDeletedPresence.DELETED_PRESENCE_FILTER);

        ProviderManager.addExtensionProvider(ChatStateExtension.ELEMENT,
                ChatStateExtension.NAMESPACE, new ChatStateExtension.Provider());
        ProviderManager.addExtensionProvider(ChangeAvatarExtension.ELEMENT,
                ChangeAvatarExtension.NAMESPACE, ChangeAvatarExtension.PROVIDER);
        ProviderManager.addExtensionProvider(DeleteMessageExtension.ELEMENT,
                DeleteMessageExtension.NAMESPACE, new DeleteMessageExtension.Provider());

        MultiUserChatManager.getInstanceFor(connection).addInvitationListener(XmppGlobalEventEmitter.this::onChatInvited);

        Roster.getInstanceFor(connection).addRosterListener(rosterListener);
    }

    private RosterListener rosterListener = new RosterListener() {
        @Override
        public void entriesAdded(Collection<String> addresses) {
            notifyFriendsAdded(Queryable.from(addresses).map(JidCreatorHelper::obtainId).toList());
        }

        @Override
        public void entriesUpdated(Collection<String> addresses) {

        }

        @Override
        public void entriesDeleted(Collection<String> addresses) {
            notifyFriendsRemoved(Queryable.from(addresses).map(JidCreatorHelper::obtainId).toList());
        }

        @Override
        public void presenceChanged(Presence presence) {

        }
    };

    private void onChatInvited(XMPPConnection conn, MultiUserChat room, String inviter, String reason, String password, Message message) {
        notifyReceiveInvite(JidCreatorHelper.obtainId(room.getRoom()));
    }

    public void interceptOutgoingMessages(com.messenger.messengerservers.model.Message message) {
        // TODO: 1/7/16 set fromId in chat
        message.setFromId(facade.getUsername());
        notifyGlobalMessage(message, EVENT_OUTGOING);
    }

    public void interceptPreOutgoingMessages(com.messenger.messengerservers.model.Message message) {
        // TODO: 1/7/16 set fromId in chat
        message.setFromId(facade.getUsername());
        notifyGlobalMessage(message, EVENT_PRE_OUTGOING);
    }

    public void interceptErrorMessage(com.messenger.messengerservers.model.Message message) {
        message.setFromId(facade.getUsername());
        notifyGlobalMessage(message, EVENT_OUTGOING_ERROR);
    }

    private void interceptIncomingMessage(Stanza packet) {
        if (!facade.isActive()) return;

        Message messageXMPP = (Message) packet;
        int packetType = stanzaType(packet);
        if (isMessageIgnored(messageXMPP, packetType)) return;
        switch (packetType) {
            case EXTENTION_STATUS:
                ChatStateExtension extension = (ChatStateExtension) messageXMPP.getExtension(ChatStateExtension.NAMESPACE);
                String from = messageXMPP.getFrom();
                if (((Message) packet).getType() == Message.Type.chat) {
                    //CRUTCH! There is no thread element in iOS message packet. So we obtain single chat thread from from and to ids
                    String fromUserId = JidCreatorHelper.obtainId(from);
                    String toUserId = JidCreatorHelper.obtainId(messageXMPP.getTo());
                    String thread = ThreadCreatorHelper.obtainThreadSingleChat(fromUserId, toUserId);

                    notifyOnChatStateChangedListener(thread, fromUserId, extension.getChatState());
                } else {
                    notifyOnChatStateChangedListener(JidCreatorHelper.obtainId(from),
                            JidCreatorHelper.obtainUserIdFromGroupJid(from), extension.getChatState());
                }
                break;
            case EXTENTION_AVATAR:
                ChangeAvatarExtension changeAvatarExtension = (ChangeAvatarExtension) messageXMPP
                        .getExtension(ChangeAvatarExtension.NAMESPACE);
                notifyOnAvatarStateChangedListener(JidCreatorHelper.obtainId(messageXMPP.getFrom()),
                        changeAvatarExtension.getAvatarUrl());
                break;
            case MESSAGE:
                com.messenger.messengerservers.model.Message message = messageConverter.convert(messageXMPP);
                if (message.getFromId() == null) {
                    break; // cause server sends type error and returns this message in history
                }
                notifyGlobalMessage(message, EVENT_INCOMING);
                break;
            case SUBJECT:
                notifyOnSubjectChanges(JidCreatorHelper.obtainId(packet.getFrom()), ((Message) packet).getSubject());
                break;
        }
    }

    private void interceptIncomingMessageDeletedPresence(Stanza stanza) {
        MessageDeletedPresence messageDeletedPresence = (MessageDeletedPresence) stanza;
        DeleteMessageExtension extension = (DeleteMessageExtension )messageDeletedPresence
                .getExtension(DeleteMessageExtension.NAMESPACE);
        notifyMessagesDeleted(extension.getDeletedMessageList());
    }


    private void interceptIncomingPresence(Stanza stanza) {
        Presence presence = (Presence) stanza;
        Type presenceType = presence.getType();
        if (presenceType == null) return;

        String fromJid = stanza.getFrom();
        boolean processed = processGroupChatParticipantsActions(presence, fromJid);
        if (!processed && (Type.available == presenceType || Type.unavailable == presenceType)) {
            processIncomingPresence(JidCreatorHelper.obtainId(fromJid), Type.available == presenceType);
        }
    }

    private void processIncomingPresence(String userId, boolean online ) {
        // if our offline presence from another device and the current device is online we should resend online presence
        if (TextUtils.equals(facade.getUsername(), userId) && !online && facade.isConnected()) {
            facade.sendInitialPresence();
        } else {
            notifyUserPresenceChanged(userId, online);
        }
    }

    private boolean processGroupChatParticipantsActions(Presence presence, String fromJid) {
        MUCUser extension = (MUCUser) presence.getExtension(MUCUser.NAMESPACE);
        if (extension == null || !JidCreatorHelper.isGroupJid(fromJid))
            return false;
        //
        String conversationId = JidCreatorHelper.obtainId(fromJid);
        String jid = extension.getItem().getJid();
        String userId = jid == null ?
                JidCreatorHelper.obtainUserIdFromGroupJid(fromJid) : JidCreatorHelper.obtainId(jid);
        //
        MUCItem item = extension.getItem();
        MUCAffiliation affiliation = item.getAffiliation();
        //

        boolean isOnline = presence.getType() == Type.available;
        notifyOnChatJoinedListener(ImmutableParticipant.builder()
                .userId(userId)
                .conversationId(conversationId)
                .affiliation(String.valueOf(affiliation))
                .build(), isOnline);
        return true;
    }

    private void interceptLeftPresence(Stanza stanza) {
        String fromJid = stanza.getFrom();

        MUCUser extension = (MUCUser) stanza.getExtension(MUCUser.NAMESPACE);
        String conversationId = JidCreatorHelper.obtainId(fromJid);

        MUCItem item = extension.getItem();
        String userJid = item.getJid();

        String userId = userJid == null ?
                JidCreatorHelper.obtainUserIdFromGroupJid(fromJid) : JidCreatorHelper.obtainId(userJid);

        notifyOnChatLeftListener(conversationId, userId);
    }

    private boolean isMessageIgnored(Message message, int packetType) {
        if (packetType == XmppPacketDetector.EXTENTION_AVATAR) {
            return false;
        }
        boolean ownMessage = message.getType() == Message.Type.groupchat
                && JidCreatorHelper.obtainId(message.getTo()).equals(JidCreatorHelper.obtainUserIdFromGroupJid(message.getFrom()));
        boolean delayed = message.getExtension("urn:xmpp:delay") != null;
        return ownMessage || delayed;
    }
}
