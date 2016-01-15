package com.messenger.delegate;

import android.support.annotation.Nullable;

import com.messenger.messengerservers.MessengerServerFacade;
import com.messenger.messengerservers.entities.Conversation;
import com.messenger.messengerservers.entities.User;
import com.messenger.messengerservers.xmpp.util.ThreadCreatorHelper;
import com.raizlabs.android.dbflow.sql.language.Select;
import com.raizlabs.android.dbflow.structure.provider.ContentUtils;
import com.worldventures.dreamtrips.BuildConfig;

import org.apache.commons.lang3.StringUtils;

import java.util.List;
import java.util.UUID;

import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

public class ChatDelegate {

    User currentUser;
    MessengerServerFacade messengerServerFacade;

    public ChatDelegate(User currentUser, MessengerServerFacade messengerServerFacade) {
        this.currentUser = currentUser;
        this.messengerServerFacade = messengerServerFacade;
    }

    public Conversation createNewConversation(List<User> participants, @Nullable String subject) {
    if (BuildConfig.DEBUG && participants.size() < 1) throw new RuntimeException();
        return participants.size() == 1 ?
                createSingleChat(participants.get(0)) : createMultiUserChat(participants, subject);
    }

    private Conversation createSingleChat(User participant) {
        return new Conversation.Builder()
                .type(Conversation.Type.CHAT)
                .id(ThreadCreatorHelper.obtainThreadSingleChat(currentUser, participant))
                .ownerId(currentUser.getUserName())
                .lastActiveDate(System.currentTimeMillis())
                .build();
    }

    private Conversation createMultiUserChat(List<User> participans, @Nullable String subject){
        Conversation conversation = new Conversation.Builder()
                .type(Conversation.Type.GROUP)
                .id(UUID.randomUUID().toString())
                .ownerId(currentUser.getUserName())
                .lastActiveDate(System.currentTimeMillis())
                .build();

        return setMultiUserChatData(conversation, participans, subject);
    }

    public Conversation modifyConversation(Conversation conversation, List<User> existParticipants,  List<User> newChatUsers, @Nullable String subject) {
        if (conversation.getType().equals(Conversation.Type.CHAT)) {
            conversation = new Conversation.Builder()
                    .ownerId(currentUser.getId())
                    .type(Conversation.Type.GROUP)
                    .lastActiveDate(System.currentTimeMillis())
                    .id(UUID.randomUUID().toString())
                    .build();
            // since we create new group chat
            // make sure to invite original participant (addressee) from old single chat
            newChatUsers.addAll(existParticipants);
            newChatUsers.add(currentUser);
        }

        return setMultiUserChatData(conversation, newChatUsers, subject);
    }

    public Conversation getExistingSingleConverastion(User participant) {
        String conversationId = ThreadCreatorHelper.obtainThreadSingleChat(currentUser, participant);
        Conversation existingConversation = new Select()
                .from(Conversation.class)
                .byIds(conversationId)
                .querySingle();

        return existingConversation;
    }

    public Conversation setMultiUserChatData(Conversation conversation, List<User> newParticipants, @Nullable String subject) {
        messengerServerFacade.getChatManager()
                .createMultiUserChatObservable(conversation.getId(), currentUser.getId())
                .doOnNext(multiUserChat -> multiUserChat.invite(newParticipants))
                .flatMap(multiUserChat -> multiUserChat.setSubject(subject))
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(multiUserChat1 -> {
                    if (!StringUtils.isEmpty(subject)) conversation.setSubject(subject);
                    //
                    ContentUtils.insert(Conversation.CONTENT_URI, conversation);
                });

        return conversation;
    }
}
