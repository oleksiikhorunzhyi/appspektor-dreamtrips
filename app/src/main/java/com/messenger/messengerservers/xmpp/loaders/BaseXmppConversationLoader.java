package com.messenger.messengerservers.xmpp.loaders;

import com.messenger.messengerservers.constant.ConversationType;
import com.messenger.messengerservers.loaders.Loader;
import com.messenger.messengerservers.model.Conversation;
import com.messenger.messengerservers.model.Participant;
import com.messenger.messengerservers.xmpp.XmppServerFacade;
import com.messenger.messengerservers.xmpp.util.ParticipantProvider;
import com.messenger.messengerservers.xmpp.util.ThreadCreatorHelper;

import java.util.List;

import rx.Observable;
import timber.log.Timber;

import static com.messenger.messengerservers.constant.ConversationType.CHAT;

public abstract class BaseXmppConversationLoader extends Loader<Conversation> {

    protected final XmppServerFacade facade;

    public BaseXmppConversationLoader(XmppServerFacade facade) {
        this.facade = facade;
    }

    protected void obtainParticipantsAndReport(List<Conversation> conversations) {
        obtainParticipants(conversations)
                .subscribe(conversationWithParticipants -> {
                    Timber.i("Conversations loaded: %s", conversations);
                    notifyListeners(conversationWithParticipants);
                }, throwable -> Timber.e(throwable, "Exception while loading conversations"));
    }

    protected Observable<List<Conversation>> obtainParticipants(List<Conversation> conversations) {
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
                            if (groupChatInvalid(conversation, owner, members)) {
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

    protected void notifyListeners(List<Conversation> conversations) {
        if (persister != null) {
            persister.save(conversations);
        }
        if (onEntityLoadedListener != null) {
            onEntityLoadedListener.onLoaded(conversations);
        }
    }
}
