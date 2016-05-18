package com.messenger.messengerservers.xmpp.loaders;

import com.messenger.messengerservers.constant.ConversationType;
import com.messenger.messengerservers.model.Conversation;
import com.messenger.messengerservers.model.Participant;
import com.messenger.messengerservers.xmpp.XmppServerFacade;
import com.messenger.messengerservers.xmpp.util.ParticipantProvider;
import com.messenger.messengerservers.xmpp.util.ThreadCreatorHelper;

import java.util.List;

import rx.Observable;
import timber.log.Timber;

import static com.messenger.messengerservers.constant.ConversationType.CHAT;

abstract class BaseConversationsLoader {
    protected final XmppServerFacade facade;

    public BaseConversationsLoader(XmppServerFacade facade) {
        this.facade = facade;
    }

    protected Observable<List<Conversation>> obtainParticipants(ParticipantProvider provider, List<Conversation> conversations) {
        return Observable.from(conversations)
                .filter(c -> !hasNoOtherUsers(c))
                .flatMap(conversation -> obtainParticipants(provider, conversation))
                .toList();
    }

    protected Observable<Conversation> obtainParticipants(ParticipantProvider provider, Conversation conversation) {
        return Observable.<Conversation>create(subscriber -> {
            if (conversation.getType().equals(CHAT)) {
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
                    if (BaseConversationsLoader.this.groupChatInvalid(conversation, owner, members)) {
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
        });
    }

    private boolean groupChatInvalid(Conversation conversation, Participant owner, List<Participant> members) {
        boolean withoutOwner = owner == null && (conversation.getType().equals(ConversationType.CHAT) || conversation.getType().equals(ConversationType.GROUP));
        boolean noParticipants = owner == null && (members == null || members.isEmpty());
        return withoutOwner || noParticipants;
    }

    private boolean singleChatInvalid(Conversation conversation, String userId) {
        return !conversation.getId().contains(userId);
    }

    private boolean hasNoOtherUsers(Conversation conversation) {
        String companion = ThreadCreatorHelper.obtainCompanionFromSingleChat(conversation.getId(), facade.getUsername());
        return companion == null;
    }

    protected Observable<ParticipantProvider> createParticipantProvider() {
        return facade.getConnectionObservable()
                .take(1)
                .map(ParticipantProvider::new);
    }
}
