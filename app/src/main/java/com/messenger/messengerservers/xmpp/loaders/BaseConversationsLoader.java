package com.messenger.messengerservers.xmpp.loaders;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.TextUtils;

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
                provider.loadMultiUserChatParticipants(conversation.getId(), members -> {
                    if (subscriber.isUnsubscribed()) return;
                    if (groupChatInvalid(conversation, members)) {
                        Timber.w("Group Conversation is invalid: %s", conversation);
                        subscriber.onCompleted();
                        return;
                    }
                    //
                    conversation.setOwnerId(findOwnerId(members));
                    conversation.getParticipants().addAll(members);
                    subscriber.onNext(conversation);
                    subscriber.onCompleted();
                });
            }
        });
    }

    // TODO: 5/24/16 this logic should be refactored, because conversation can have a few owners.
    @Nullable
    private String findOwnerId(List<Participant> participants) {
        for (Participant p : participants) {
            if (TextUtils.equals(p.getAffiliation(), Participant.Affiliation.OWNER)) {
                return p.getUserId();
            }
        }
        return null;
    }

    private boolean groupChatInvalid(@NonNull Conversation conversation, @NonNull List<Participant> members) {
        boolean isGroupChat = !TextUtils.equals(conversation.getType(), ConversationType.CHAT);
        return isGroupChat && members.size() < 2;
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
