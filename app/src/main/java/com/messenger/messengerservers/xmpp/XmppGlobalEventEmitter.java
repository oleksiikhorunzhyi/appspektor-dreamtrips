package com.messenger.messengerservers.xmpp;

import android.text.TextUtils;

import com.innahema.collections.query.queriables.Queryable;
import com.messenger.messengerservers.GlobalEventEmitter;
import com.messenger.messengerservers.event.ImmutableClearChatEvent;
import com.messenger.messengerservers.event.ImmutableRevertClearingEvent;
import com.messenger.messengerservers.model.ImmutableParticipant;
import com.messenger.messengerservers.xmpp.extensions.ChangeAvatarExtension;
import com.messenger.messengerservers.xmpp.extensions.ChatStateExtension;
import com.messenger.messengerservers.xmpp.extensions.DeleteMessageExtension;
import com.messenger.messengerservers.xmpp.extensions.SystemMessageExtension;
import com.messenger.messengerservers.xmpp.filter.incoming.IncomingMessageFilter;
import com.messenger.messengerservers.xmpp.filter.incoming.IncomingMessageFilterType;
import com.messenger.messengerservers.xmpp.providers.ClearIQProvider;
import com.messenger.messengerservers.xmpp.stanzas.ClearChatIQ;
import com.messenger.messengerservers.xmpp.stanzas.PresenceStatus;
import com.messenger.messengerservers.xmpp.stanzas.RevertClearChatIQ;
import com.messenger.messengerservers.xmpp.stanzas.incoming.LeftRoomPresence;
import com.messenger.messengerservers.xmpp.stanzas.incoming.MessageDeletedPresence;
import com.messenger.messengerservers.xmpp.util.JidCreatorHelper;
import com.messenger.messengerservers.xmpp.util.ThreadCreatorHelper;
import com.messenger.messengerservers.xmpp.util.XmppMessageConverter;

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

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

import rx.Observable;
import timber.log.Timber;

import static com.messenger.messengerservers.xmpp.util.XmppPacketDetector.EXTENTION_AVATAR;
import static com.messenger.messengerservers.xmpp.util.XmppPacketDetector.EXTENTION_STATUS;
import static com.messenger.messengerservers.xmpp.util.XmppPacketDetector.EXTENTION_SYSTEM_MESSAGE;
import static com.messenger.messengerservers.xmpp.util.XmppPacketDetector.MESSAGE;
import static com.messenger.messengerservers.xmpp.util.XmppPacketDetector.SUBJECT;
import static com.messenger.messengerservers.xmpp.util.XmppPacketDetector.stanzaType;

public class XmppGlobalEventEmitter extends GlobalEventEmitter {
    private final Map<IncomingMessageFilterType, List<IncomingMessageFilter>> filters;
    private XmppServerFacade facade;
    private XmppMessageConverter messageConverter;

    public XmppGlobalEventEmitter(Map<IncomingMessageFilterType, List<IncomingMessageFilter>> filters) {
        this.filters = filters;
        initProviders();
    }

    public void setFacade(XmppServerFacade facade) {
        this.messageConverter = new XmppMessageConverter(facade.getGson());
        this.facade = facade;
        facade.getConnectionObservable()
                .subscribe(this::prepareManagers);
    }

    private void initProviders() {
        ProviderManager.addExtensionProvider(ChatStateExtension.ELEMENT,
                ChatStateExtension.NAMESPACE, new ChatStateExtension.Provider());
        ProviderManager.addExtensionProvider(ChangeAvatarExtension.ELEMENT,
                ChangeAvatarExtension.NAMESPACE, ChangeAvatarExtension.PROVIDER);
        ProviderManager.addExtensionProvider(DeleteMessageExtension.ELEMENT,
                DeleteMessageExtension.NAMESPACE, new DeleteMessageExtension.Provider());
        ProviderManager.addExtensionProvider(SystemMessageExtension.ELEMENT,
                SystemMessageExtension.NAMESPACE, SystemMessageExtension.PROVIDER);
        ProviderManager.addIQProvider(ClearIQProvider.ELEMENT_QUERY,
                ClearIQProvider.NAME_SPACE, new ClearIQProvider());
    }

    private void prepareManagers(XMPPConnection connection) {
        connection.addAsyncStanzaListener(this::filterAndInterceptIncomingMessage,
                StanzaTypeFilter.MESSAGE);
        connection.addAsyncStanzaListener(this::filterAndInterceptIncomingPresence,
                StanzaTypeFilter.PRESENCE);
        connection.addAsyncStanzaListener(this::filterAndInterceptLeftPresence,
                LeftRoomPresence.LEAVE_PRESENCE_FILTER);
        connection.addAsyncStanzaListener(this::filterAndInterceptIncomingMessageDeletedPresence,
                MessageDeletedPresence.DELETED_PRESENCE_FILTER);
        connection.addAsyncStanzaListener(this::interceptClearChatIncomingStanza,
                new StanzaTypeFilter(ClearChatIQ.class));
        connection.addAsyncStanzaListener(this::interceptRevertClearingChatIncomingStanza,
                new StanzaTypeFilter(RevertClearChatIQ.class));

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

    private Observable<Stanza> filterIncomingStanzaWithType(IncomingMessageFilterType type, Stanza packet) {
        List<IncomingMessageFilter> packetFilters = new ArrayList<>();
        if (filters.get(IncomingMessageFilterType.ALL) != null) {
            packetFilters.addAll(filters.get(IncomingMessageFilterType.ALL));
        }
        if (filters.get(type) != null) packetFilters.addAll(filters.get(type));

        if (packetFilters.isEmpty()) return Observable.just(packet);
        return Observable.from(packetFilters)
                .flatMap(filter -> filter.skipPacket(packet))
                // provide default accumulated value value of false for reduce()
                .reduce(false, (accumulatedValue, accumulatingValue) -> accumulatedValue || accumulatingValue)
                // invert as filter returns true if we should skip the message
                .filter(shouldSkipMessage -> !shouldSkipMessage)
                .map(val -> packet);
    }

    private void filterAndInterceptIncomingMessage(Stanza packet) {
        if (!facade.isActive()) return;
        filterIncomingStanzaWithType(IncomingMessageFilterType.MESSAGE, packet)
                .subscribe(this::interceptIncomingMessage,
                        e -> Timber.e(e, "Filters -- Error during filtering message"));
    }

    private void interceptIncomingMessage(Stanza packet) {
        Message messageXMPP = (Message) packet;
        int packetType = stanzaType(packet);
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
            case EXTENTION_SYSTEM_MESSAGE:
                SystemMessageExtension systemMessageExtension =
                        messageXMPP.getExtension(SystemMessageExtension.ELEMENT, SystemMessageExtension.NAMESPACE);
                com.messenger.messengerservers.model.Message systemMessage =
                        messageConverter.convertSystemMessage(messageXMPP, systemMessageExtension);
                notifyGlobalMessage(systemMessage, EVENT_INCOMING);
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

    private void filterAndInterceptIncomingMessageDeletedPresence(Stanza stanza) {
        filterIncomingStanzaWithType(IncomingMessageFilterType.DELETE_PRESENCE, stanza)
                .subscribe(this::interceptIncomingMessageDeletedPresence,
                        e -> Timber.e(e, "Filters -- Error during filtering deleted presence"));
    }

    private void interceptIncomingMessageDeletedPresence(Stanza stanza) {
        MessageDeletedPresence messageDeletedPresence = (MessageDeletedPresence) stanza;
        DeleteMessageExtension extension = (DeleteMessageExtension) messageDeletedPresence
                .getExtension(DeleteMessageExtension.NAMESPACE);
        notifyMessagesDeleted(extension.getDeletedMessageList());
    }

    private void filterAndInterceptIncomingPresence(Stanza stanza) {
        filterIncomingStanzaWithType(IncomingMessageFilterType.PRESENCE, stanza)
                .subscribe(this::interceptIncomingPresence,
                        e -> Timber.e(e, "Filters -- Error during filtering presence"));
    }

    private void interceptClearChatIncomingStanza(Stanza stanza) {
        ClearChatIQ clearIq = (ClearChatIQ) stanza;
        notifyOnClearChatEventListener(ImmutableClearChatEvent.builder()
                .conversationId(clearIq.getChatId())
                .clearTime(clearIq.getClearDate())
                .build()
        );
    }

    private void interceptRevertClearingChatIncomingStanza(Stanza stanza) {
        RevertClearChatIQ revertIq = (RevertClearChatIQ) stanza;
        notifyOnRevertClearingEventListener(ImmutableRevertClearingEvent.builder()
                .conversationId(revertIq.getChatId())
                .build()
        );
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

    private void processIncomingPresence(String userId, boolean online) {
        // if our offline presence from another device and the current device is online we should resend online presence
        if (TextUtils.equals(facade.getUsername(), userId) && !online && facade.isConnected()) {
            facade.sendInitialPresence();
        } else {
            notifyUserPresenceChanged(userId, online);
        }
    }

    private boolean processGroupChatParticipantsActions(Presence presence, String fromJid) {
        MUCUser mucUser = (MUCUser) presence.getExtension(MUCUser.NAMESPACE);
        if (mucUser == null || !JidCreatorHelper.isGroupJid(fromJid))
            return false;
        //
        String conversationId = JidCreatorHelper.obtainId(fromJid);
        MUCItem item = mucUser.getItem();
        MUCAffiliation affiliation = item.getAffiliation();

        if (isKickPresence(presence.getType(), affiliation)) {
            notifyOnKickListener(conversationId, JidCreatorHelper.obtainUserIdFromGroupJid(fromJid));
            return true;
        } else if (isInvitePresence(presence)) {
            String jid = mucUser.getItem().getJid();
            String userId = jid == null ?
                    JidCreatorHelper.obtainUserIdFromGroupJid(fromJid) : JidCreatorHelper.obtainId(jid);

            boolean isOnline = presence.getType() == Type.available;

            notifyOnChatJoinedListener(ImmutableParticipant.builder()
                    .userId(userId)
                    .conversationId(conversationId)
                    .affiliation(String.valueOf(affiliation))
                    .build(), isOnline);
            return true;
        }
        return false;
    }

    private boolean isInvitePresence(Presence presence) {
        return TextUtils.equals(presence.getStatus(), PresenceStatus.INVITED);
    }

    private boolean isKickPresence(Type type, MUCAffiliation affiliation) {
        return type == Type.unavailable && affiliation == MUCAffiliation.none;
    }

    private void filterAndInterceptLeftPresence(Stanza stanza) {
        filterIncomingStanzaWithType(IncomingMessageFilterType.LEAVE_PRESENCE, stanza)
                .subscribe(this::interceptLeftPresence,
                        e -> Timber.e(e, "Filters -- Error during filtering left presence"));
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
}
