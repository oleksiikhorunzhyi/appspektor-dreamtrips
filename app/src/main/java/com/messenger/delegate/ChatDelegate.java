package com.messenger.delegate;

import android.support.annotation.Nullable;
import android.text.TextUtils;

import com.innahema.collections.query.queriables.Queryable;
import com.messenger.entities.DataConversation;
import com.messenger.entities.DataUser;
import com.messenger.messengerservers.MessengerServerFacade;
import com.messenger.messengerservers.constant.ConversationStatus;
import com.messenger.messengerservers.constant.ConversationType;
import com.messenger.messengerservers.xmpp.util.ThreadCreatorHelper;
import com.messenger.storage.dao.ConversationsDAO;
import com.worldventures.dreamtrips.BuildConfig;

import java.util.List;
import java.util.UUID;

import rx.Observable;

public class ChatDelegate {
    private final String currentUserId;
    private final MessengerServerFacade messengerServerFacade;
    private final ConversationNameDelegate conversationNameDelegate;

    public ChatDelegate(String currentUserId, MessengerServerFacade messengerServerFacade,
                        ConversationNameDelegate conversationNameDelegate) {
        this.currentUserId = currentUserId;
        this.messengerServerFacade = messengerServerFacade;
        this.conversationNameDelegate = conversationNameDelegate;
    }

    public Observable<DataConversation> createNewConversation(List<DataUser> participants, @Nullable String subject) {
    if (BuildConfig.DEBUG && participants.size() < 1) throw new RuntimeException();
        return participants.size() == 1 ?
                createSingleChat(participants.get(0).getId()) : createMultiUserChat(participants, subject);
    }

    private Observable<DataConversation> createSingleChat(String participantId) {
        return Observable.just(new DataConversation.Builder()
                .type(ConversationType.CHAT)
                .id(ThreadCreatorHelper.obtainThreadSingleChat(currentUserId, participantId))
                .ownerId(currentUserId)
                .lastActiveDate(System.currentTimeMillis())
                .status(ConversationStatus.PRESENT)
                .build());
    }

    private Observable<DataConversation> createMultiUserChat(List<DataUser> participants, @Nullable String subject){
        DataConversation conversation = new DataConversation.Builder()
                .type(ConversationType.GROUP)
                .id(UUID.randomUUID().toString())
                .ownerId(currentUserId)
                .lastActiveDate(System.currentTimeMillis())
                .status(ConversationStatus.PRESENT)
                .subject(TextUtils.isEmpty(subject)? null : subject)
                .build();

        return setMultiUserChatData(conversation, participants, subject);
    }

    public Observable<DataConversation> modifyConversation(DataConversation conversation, List<DataUser> existParticipants,
                                                           List<DataUser> newChatUserIds, @Nullable String subject) {
        if (conversation.getType().equals(ConversationType.CHAT)) {
            conversation = new DataConversation.Builder()
                    .ownerId(currentUserId)
                    .type(ConversationType.GROUP)
                    .status(ConversationStatus.PRESENT)
                    .lastActiveDate(System.currentTimeMillis())
                    .id(UUID.randomUUID().toString())
                    .build();
            // since we create new group chat
            // make sure to invite original participant (addressee) from old single chat
            newChatUserIds.addAll(existParticipants);
        }

        return setMultiUserChatData(conversation, newChatUserIds, subject);
    }

    public DataConversation getExistingSingleConversation(String participantId) {
        String conversationId = ThreadCreatorHelper.obtainThreadSingleChat(currentUserId, participantId);
        DataConversation existingConversation = ConversationsDAO.getConversationById(conversationId);
        return existingConversation;
    }

    private Observable<DataConversation> setMultiUserChatData(DataConversation conversation,
                                                              List<DataUser> newParticipants, @Nullable String subject) {
        return messengerServerFacade.getChatManager()
                .createMultiUserChatObservable(conversation.getId(), currentUserId)
                .doOnNext(multiUserChat -> multiUserChat.invite(getIdFromUsers(newParticipants)))
                .flatMap(multiUserChat -> multiUserChat.setSubject(subject))
                .map(chat -> conversation)
                .doOnNext(dataConversation -> dataConversation
                        .setDefaultSubject(conversationNameDelegate.obtainGroupConversationName(newParticipants)));
    }

    private List<String> getIdFromUsers(List<DataUser> dataUsers) {
        return Queryable.from(dataUsers).map(DataUser::getId).toList();
    }
}
