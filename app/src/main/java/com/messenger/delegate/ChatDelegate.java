package com.messenger.delegate;

import android.support.annotation.Nullable;
import android.text.TextUtils;

import com.innahema.collections.query.queriables.Queryable;
import com.messenger.entities.DataConversation;
import com.messenger.entities.DataUser;
import com.messenger.messengerservers.ConversationIdHelper;
import com.messenger.messengerservers.MessengerServerFacade;
import com.messenger.messengerservers.constant.ConversationStatus;
import com.messenger.messengerservers.constant.ConversationType;
import com.messenger.messengerservers.xmpp.util.ThreadCreatorHelper;
import com.messenger.storage.dao.ConversationsDAO;
import com.techery.spares.session.SessionHolder;
import com.worldventures.dreamtrips.BuildConfig;
import com.worldventures.dreamtrips.core.session.UserSession;

import java.util.List;
import java.util.UUID;

import rx.Observable;

public class ChatDelegate {
    private final SessionHolder<UserSession> appSessionHolder;
    private final MessengerServerFacade messengerServerFacade;

    private final ConversationIdHelper conversationIdHelper = new ConversationIdHelper();

    public ChatDelegate(SessionHolder<UserSession> appSessionHolder, MessengerServerFacade messengerServerFacade) {
        this.appSessionHolder = appSessionHolder;
        this.messengerServerFacade = messengerServerFacade;
    }

    public Observable<DataConversation> createNewConversation(List<DataUser> participants, @Nullable String subject) {
    if (BuildConfig.DEBUG && participants.size() < 1) throw new RuntimeException();
        return participants.size() == 1 ?
                createSingleChat(participants.get(0).getId()) : createMultiUserChat(participants, subject);
    }

    private Observable<DataConversation> createSingleChat(String participantId) {
        return Observable.just(new DataConversation.Builder()
                .type(ConversationType.CHAT)
                .id(ThreadCreatorHelper.obtainThreadSingleChat(getCurrentUserId(), participantId))
                .ownerId(getCurrentUserId())
                .lastActiveDate(System.currentTimeMillis())
                .status(ConversationStatus.PRESENT)
                .build());
    }

    private Observable<DataConversation> createMultiUserChat(List<DataUser> participants, @Nullable String subject){
        DataConversation conversation = new DataConversation.Builder()
                .type(ConversationType.GROUP)
                .id(UUID.randomUUID().toString())
                .ownerId(getCurrentUserId())
                .lastActiveDate(System.currentTimeMillis())
                .status(ConversationStatus.PRESENT)
                .subject(TextUtils.isEmpty(subject)? null : subject)
                .build();

        return setMultiUserChatData(conversation, participants, subject);
    }

    public Observable<DataConversation> modifyConversation(DataConversation conversation, List<DataUser> existParticipants,
                                                           List<DataUser> newChatUserIds, @Nullable String subject) {
        if (TextUtils.equals(conversation.getType(), ConversationType.CHAT)) {
            conversation = new DataConversation.Builder()
                    .ownerId(getCurrentUserId())
                    .type(ConversationType.GROUP)
                    .status(ConversationStatus.PRESENT)
                    .subject(TextUtils.isEmpty(subject)? null : subject)
                    .lastActiveDate(System.currentTimeMillis())
                    .id(UUID.randomUUID().toString())
                    .build();
            // since we create new group chat
            // make sure to invite original participant (addressee) from old single chat
            newChatUserIds.addAll(existParticipants);
        }

        return setMultiUserChatData(conversation, newChatUserIds, subject);
    }

    public Observable<DataConversation> createConversation(String conversationId, String currentUserId){
        return Observable.just(conversationId)
                .map(convId -> {
                    DataConversation conversation = new DataConversation.Builder()
                            .id(convId)
                            .lastActiveDate(System.currentTimeMillis())
                            .status(ConversationStatus.PRESENT)
                            .type(conversationIdHelper.obtainType(convId, currentUserId))
                            .build();

                    return conversation;
                });
    }

    public DataConversation getExistingSingleConversation(String participantId) {
        String conversationId = ThreadCreatorHelper.obtainThreadSingleChat(getCurrentUserId(), participantId);
        DataConversation existingConversation = ConversationsDAO.getConversationById(conversationId);
        return existingConversation;
    }

    private Observable<DataConversation> setMultiUserChatData(DataConversation conversation,
                                                              List<DataUser> newParticipants, @Nullable String subject) {
        return messengerServerFacade.getChatManager()
                .createMultiUserChatObservable(conversation.getId(), getCurrentUserId())
                .doOnNext(multiUserChat -> multiUserChat.invite(getUserIds(newParticipants)))
                .flatMap(multiUserChat -> multiUserChat.setSubject(subject))
                .map(chat -> conversation);
    }

    private String getCurrentUserId() {
        if (!appSessionHolder.get().isPresent()) return "";
        return appSessionHolder.get().get().getUser().getUsername();
    }

    private List<String> getUserIds(List<DataUser> dataUsers) {
        return Queryable.from(dataUsers).map(DataUser::getId).toList();
    }
}
