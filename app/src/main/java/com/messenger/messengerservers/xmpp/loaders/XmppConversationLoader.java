package com.messenger.messengerservers.xmpp.loaders;

import com.messenger.messengerservers.entities.Conversation;
import com.messenger.messengerservers.entities.ConversationData;
import com.messenger.messengerservers.entities.Participant;
import com.messenger.messengerservers.entities.User;
import com.messenger.messengerservers.loaders.Loader;
import com.messenger.messengerservers.xmpp.XmppServerFacade;
import com.messenger.messengerservers.xmpp.entities.ConversationWithLastMessage;
import com.messenger.messengerservers.xmpp.packets.ConversationsPacket;
import com.messenger.messengerservers.xmpp.packets.ObtainConversationListPacket;
import com.messenger.messengerservers.xmpp.providers.ConversationProvider;
import com.messenger.messengerservers.xmpp.util.ParticipantProvider;
import com.messenger.messengerservers.xmpp.util.ThreadCreatorHelper;

import org.jivesoftware.smack.SmackException;
import org.jivesoftware.smack.packet.IQ;
import org.jivesoftware.smack.provider.ProviderManager;

import java.util.List;

import rx.Observable;
import timber.log.Timber;

import static com.messenger.messengerservers.entities.Conversation.Type.CHAT;

public class XmppConversationLoader extends Loader<ConversationData> {

    private static final int MAX_CONVERSATIONS = 512;
    private final XmppServerFacade facade;

    public XmppConversationLoader(XmppServerFacade facade) {
        this.facade = facade;
        ProviderManager.addIQProvider(
                ConversationsPacket.ELEMENT_LIST, ConversationsPacket.NAMESPACE,
                new ConversationProvider()
        );
    }

    @Override
    public void load() {
        ObtainConversationListPacket packet = new ObtainConversationListPacket();
        packet.setMax(MAX_CONVERSATIONS);
        packet.setType(IQ.Type.get);

        try {
            facade.getConnection().sendStanzaWithResponseCallback(packet,
                    (stanza) -> stanza instanceof ConversationsPacket,
                    (stanzaPacket) -> {
                        List<ConversationWithLastMessage> conversations = ((ConversationsPacket) stanzaPacket).getConversations();
                        obtainConversationsWithParticipants(conversations)
                                .subscribe(conversationWithParticipants -> {
                                    Timber.i("Conversations loaded: %s", conversations);
                                    notifyListeners(conversationWithParticipants);
                                });
                    });
        } catch (SmackException.NotConnectedException e) {
            Timber.e(e, "Can't load conversations");
        }
    }

    private Observable<List<ConversationData>> obtainConversationsWithParticipants(List<ConversationWithLastMessage> conversations) {
        ParticipantProvider provider = new ParticipantProvider(facade.getConnection());
        return Observable.from(conversations)
                .filter(c -> !hasNoOtherUsers(c.conversation))
                .flatMap(conversationWithLastMessage -> Observable.<ConversationData>create(subscriber -> {
                    Conversation conversation = conversationWithLastMessage.conversation;
                    if (conversation.getType().equals(CHAT)) {
                        List<Participant> participants = provider.getSingleChatParticipants(conversation);
                        if (subscriber.isUnsubscribed()) return;
                        if (singleChatInvalid(conversation, facade.getOwner())) {
                            Timber.w("Single Conversation is invalid: %s", conversation);
                            subscriber.onCompleted();
                            return;
                        }
                        //
                        subscriber.onNext(new ConversationData(conversation, participants, conversationWithLastMessage.lastMessage));
                        subscriber.onCompleted();
                    } else {
                        provider.loadMultiUserChatParticipants(conversation, (owner, members, abandoned) -> {
                            if (subscriber.isUnsubscribed()) return;
                            if (XmppConversationLoader.this.groupChatInvalid(conversation, owner, members)) {
                                Timber.w("Group Conversation is invalid: %s", conversation);
                                subscriber.onCompleted();
                                return;
                            }
                            //
                            if (owner != null) {
                                conversation.setOwnerId(owner.getUser().getId());
                                members.add(0, owner);
                            }
                            conversation.setAbandoned(abandoned);
                            subscriber.onNext(new ConversationData(conversation, members, conversationWithLastMessage.lastMessage));
                            subscriber.onCompleted();
                        });
                    }
                }))
                .toList();
    }

    private boolean groupChatInvalid(Conversation conversation, Participant owner, List<Participant> members) {
        boolean withoutOwner = owner == null && (conversation.getType().equals(Conversation.Type.CHAT) || conversation.getType().equals(Conversation.Type.GROUP));
        boolean noParticipants = owner == null && (members == null || members.isEmpty());
        return withoutOwner || noParticipants;
    }

    private boolean singleChatInvalid(Conversation conversation, User user) {
        boolean wrongSingleChat = !conversation.getId().contains(user.getId());
        return wrongSingleChat;
    }

    private boolean hasNoOtherUsers(Conversation conversation) {
        String companion = ThreadCreatorHelper.obtainCompanionFromSingleChat(conversation, facade.getConnection().getUser());
        return companion == null;
    }

    public void notifyListeners(List<ConversationData> conversations) {
        if (persister != null) {
            persister.save(conversations);
        }
        if (onEntityLoadedListener != null) {
            onEntityLoadedListener.onLoaded(conversations);
        }
    }
}
