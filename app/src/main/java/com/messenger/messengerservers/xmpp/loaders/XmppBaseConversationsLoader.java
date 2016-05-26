package com.messenger.messengerservers.xmpp.loaders;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.TextUtils;

import com.messenger.messengerservers.constant.ConversationType;
import com.messenger.messengerservers.model.Conversation;
import com.messenger.messengerservers.model.Participant;
import com.messenger.messengerservers.xmpp.XmppServerFacade;

import java.util.List;

import rx.Observable;
import timber.log.Timber;

import static com.messenger.messengerservers.constant.ConversationType.CHAT;

abstract class XmppBaseConversationsLoader {
    protected final XmppServerFacade facade;

    public XmppBaseConversationsLoader(XmppServerFacade facade) {
        this.facade = facade;
    }

    protected Observable<List<Conversation>> obtainParticipants(XmppParticipantsLoader participantsLoader, List<Conversation> conversations) {
        return Observable.from(conversations)
                .flatMap(conversation -> obtainParticipants(participantsLoader, conversation))
                .toList();
    }

    protected Observable<Conversation> obtainParticipants(XmppParticipantsLoader participantsLoader, Conversation conversation) {
        return TextUtils.equals(conversation.getType(), CHAT) ?
                obtainSingleConversationParticipant(participantsLoader, conversation) : obtainGroupConversationParticipants(participantsLoader, conversation);
    }

    private Observable<Conversation> obtainSingleConversationParticipant(XmppParticipantsLoader participantLoader, Conversation conversation) {
        if (singleChatInvalid(conversation, facade.getUsername())) {
            Timber.w("Single Conversation is invalid: %s", conversation);
            return Observable.empty();
        }

        return participantLoader.getSingleChatParticipants(conversation.getId())
                .map(participant -> {
                    conversation.getParticipants().add(participant);
                    return conversation;
                });
    }

    private Observable<Conversation> obtainGroupConversationParticipants (XmppParticipantsLoader participantLoader, Conversation conversation) {
        return participantLoader.loadMultiUserChatParticipants(conversation.getId())
                .filter(participants -> {
                    boolean groupChatInvalid = groupChatInvalid(conversation, participants);
                    if (groupChatInvalid) {
                        Timber.w("Group Conversation is invalid: %s", conversation);
                    }
                    return !groupChatInvalid;
                })
                .map(participants -> {
                    conversation.setOwnerId(findOwnerId(participants));
                    conversation.getParticipants().addAll(participants);
                    return conversation;
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

}
