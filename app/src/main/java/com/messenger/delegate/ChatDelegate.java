package com.messenger.delegate;

import android.support.annotation.Nullable;
import android.text.TextUtils;

import com.messenger.entities.Conversation;
import com.messenger.messengerservers.MessengerServerFacade;
import com.messenger.messengerservers.constant.ConversationStatus;
import com.messenger.messengerservers.constant.ConversationType;
import com.messenger.messengerservers.xmpp.util.ThreadCreatorHelper;
import com.messenger.storage.dao.ConversationsDAO;
import com.worldventures.dreamtrips.BuildConfig;

import java.util.List;
import java.util.UUID;

import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

public class ChatDelegate {
    private final String currentUserId;
    private final MessengerServerFacade messengerServerFacade;

    public ChatDelegate(String currentUserId, MessengerServerFacade messengerServerFacade) {
        this.currentUserId = currentUserId;
        this.messengerServerFacade = messengerServerFacade;
    }

    public Conversation createNewConversation(List<String> participantIds, @Nullable String subject) {
    if (BuildConfig.DEBUG && participantIds.size() < 1) throw new RuntimeException();
        return participantIds.size() == 1 ?
                createSingleChat(participantIds.get(0)) : createMultiUserChat(participantIds, subject);
    }

    private Conversation createSingleChat(String participantId) {
        return new Conversation.Builder()
                .type(ConversationType.CHAT)
                .id(ThreadCreatorHelper.obtainThreadSingleChat(currentUserId, participantId))
                .ownerId(currentUserId)
                .lastActiveDate(System.currentTimeMillis())
                .status(ConversationStatus.PRESENT)
                .build();
    }

    private Conversation createMultiUserChat(List<String> participans, @Nullable String subject){
        Conversation conversation = new Conversation.Builder()
                .type(ConversationType.GROUP)
                .id(UUID.randomUUID().toString())
                .ownerId(currentUserId)
                .lastActiveDate(System.currentTimeMillis())
                .status(ConversationStatus.PRESENT)
                .subject(TextUtils.isEmpty(subject)? null : subject)
                .build();

        return setMultiUserChatData(conversation, participans, subject);
    }

    public Conversation modifyConversation(Conversation conversation, List<String> existParticipantIds,  List<String> newChatUserIds, @Nullable String subject) {
        if (conversation.getType().equals(ConversationType.CHAT)) {
            conversation = new Conversation.Builder()
                    .ownerId(currentUserId)
                    .type(ConversationType.GROUP)
                    .status(ConversationStatus.PRESENT)
                    .lastActiveDate(System.currentTimeMillis())
                    .id(UUID.randomUUID().toString())
                    .build();
            // since we create new group chat
            // make sure to invite original participant (addressee) from old single chat
            newChatUserIds.addAll(existParticipantIds);
            newChatUserIds.add(currentUserId);
        }

        return setMultiUserChatData(conversation, newChatUserIds, subject);
    }

    public Conversation getExistingSingleConverastion(String participantId) {
        String conversationId = ThreadCreatorHelper.obtainThreadSingleChat(currentUserId, participantId);
        Conversation existingConversation = ConversationsDAO.getConversationById(conversationId);
        return existingConversation;
    }

    public Conversation setMultiUserChatData(Conversation conversation, List<String> newParticipantIds, @Nullable String subject) {
        messengerServerFacade.getChatManager()
                .createMultiUserChatObservable(conversation.getId(), currentUserId)
                .doOnNext(multiUserChat -> multiUserChat.invite(newParticipantIds))
                .flatMap(multiUserChat -> multiUserChat.setSubject(subject))
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe();

        return conversation;
    }
}
