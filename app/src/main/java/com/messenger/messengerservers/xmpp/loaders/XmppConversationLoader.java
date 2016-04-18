package com.messenger.messengerservers.xmpp.loaders;

import com.messenger.messengerservers.constant.ConversationType;
import com.messenger.messengerservers.loaders.Loader;
import com.messenger.messengerservers.model.Conversation;
import com.messenger.messengerservers.model.Participant;
import com.messenger.messengerservers.xmpp.XmppServerFacade;
import com.messenger.messengerservers.xmpp.stanzas.ConversationsIQ;
import com.messenger.messengerservers.xmpp.stanzas.ObtainConversationListIQ;
import com.messenger.messengerservers.xmpp.providers.ConversationProvider;
import com.messenger.messengerservers.xmpp.util.ParticipantProvider;
import com.messenger.messengerservers.xmpp.util.ThreadCreatorHelper;

import org.jivesoftware.smack.SmackException;
import org.jivesoftware.smack.packet.IQ;
import org.jivesoftware.smack.provider.ProviderManager;

import java.util.List;

import rx.Observable;
import timber.log.Timber;

import static com.messenger.messengerservers.constant.ConversationType.CHAT;

public class XmppConversationLoader extends Loader<Conversation> {

    private static final int MAX_CONVERSATIONS = 512;
    private final XmppServerFacade facade;

    public XmppConversationLoader(XmppServerFacade facade) {
        this.facade = facade;
        ProviderManager.addIQProvider(
                ConversationsIQ.ELEMENT_LIST, ConversationsIQ.NAMESPACE,
                new ConversationProvider(facade.getGson())
        );
    }

    @Override
    public void load() {
        ObtainConversationListIQ packet = new ObtainConversationListIQ();
        packet.setMax(MAX_CONVERSATIONS);
        packet.setType(IQ.Type.get);

        try {
            facade.getConnection().sendStanzaWithResponseCallback(packet,
                    (stanza) -> stanza instanceof ConversationsIQ,
                    (stanzaPacket) -> {
                        List<Conversation> conversations = ((ConversationsIQ) stanzaPacket).getConversations();
                        obtainConversationsWithParticipants(conversations)
                                .subscribe(conversationWithParticipants -> {
                                    Timber.i("Conversations loaded: %s", conversations);
                                    notifyListeners(conversationWithParticipants);
                                }, throwable -> Timber.e(throwable, "Exception while loading conversations"));
                    });
        } catch (SmackException.NotConnectedException e) {
            Timber.e(e, "Can't load conversations");
        }
    }

    private Observable<List<Conversation>> obtainConversationsWithParticipants(List<Conversation> conversations) {
        ParticipantProvider provider = new ParticipantProvider(facade.getConnection());
        return Observable.from(conversations)
                .filter(c -> !hasNoOtherUsers(c))
                .flatMap(conversation -> Observable.<Conversation>create(subscriber -> {
                    if (conversation.getType().equals(CHAT)) {
                        // TODO: 2/1/16  participants has jid
                        List<Participant> participants = provider.getSingleChatParticipants(conversation.getId());
                        if (subscriber.isUnsubscribed()) return;
                        if (singleChatInvalid(conversation, facade.getUsername())) {
                            Timber.w("Single Conversation is invalid: %s", conversation);
                            subscriber.onCompleted();
                            return;
                        }

                        conversation.getParticipants().addAll(participants);
                        subscriber.onNext(conversation);
                        subscriber.onCompleted();
                    } else {
                        provider.loadMultiUserChatParticipants(conversation.getId(), (owner, members, abandoned) -> {
                            if (subscriber.isUnsubscribed()) return;
                            if (XmppConversationLoader.this.groupChatInvalid(conversation, owner, members)) {
                                Timber.w("Group Conversation is invalid: %s", conversation);
                                subscriber.onCompleted();
                                return;
                            }
                            //
                            if (owner != null) {
                                conversation.setOwnerId(owner.getUserId());
                                members.add(0, owner);
                            }
                            conversation.setAbandoned(abandoned);
                            conversation.getParticipants().addAll(members);
                            subscriber.onNext(conversation);
                            subscriber.onCompleted();
                        });
                    }
                }))
                .toList();
    }

    private boolean groupChatInvalid(Conversation conversation, Participant owner, List<Participant> members) {
        boolean withoutOwner = owner == null && (conversation.getType().equals(ConversationType.CHAT) || conversation.getType().equals(ConversationType.GROUP));
        boolean noParticipants = owner == null && (members == null || members.isEmpty());
        return withoutOwner || noParticipants;
    }

    private boolean singleChatInvalid(Conversation conversation, String userId) {
        boolean wrongSingleChat = !conversation.getId().contains(userId);
        return wrongSingleChat;
    }

    private boolean hasNoOtherUsers(Conversation conversation) {
        String companion = ThreadCreatorHelper.obtainCompanionFromSingleChat(conversation.getId(), facade.getConnection().getUser());
        return companion == null;
    }

    public void notifyListeners(List<Conversation> conversations) {
        if (persister != null) {
            persister.save(conversations);
        }
        if (onEntityLoadedListener != null) {
            onEntityLoadedListener.onLoaded(conversations);
        }
    }
}
