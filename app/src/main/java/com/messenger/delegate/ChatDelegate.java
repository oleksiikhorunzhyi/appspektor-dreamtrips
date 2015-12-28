package com.messenger.delegate;

import android.support.annotation.Nullable;
import android.text.TextUtils;

import com.messenger.messengerservers.MessengerServerFacade;
import com.messenger.messengerservers.chat.MultiUserChat;
import com.messenger.messengerservers.entities.Conversation;
import com.messenger.messengerservers.entities.User;
import com.messenger.messengerservers.xmpp.util.ThreadCreatorHelper;
import com.raizlabs.android.dbflow.sql.language.Select;

import java.util.List;
import java.util.UUID;

public class ChatDelegate {

    User currentUser;
    MessengerServerFacade messengerServerFacade;

    public ChatDelegate(User currentUser, MessengerServerFacade messengerServerFacade) {
        this.currentUser = currentUser;
        this.messengerServerFacade = messengerServerFacade;
    }

    public Conversation createNewConversation(List<User> participants, @Nullable String subject) {
        return participants.size() == 1 ?
                createSingleChat(participants.get(0)) : createMultiUserChat(participants, subject);
    }

    private Conversation createSingleChat(User participant) {
        return new Conversation.Builder()
                .type(Conversation.Type.CHAT)
                .id(ThreadCreatorHelper.obtainThreadSingleChat(currentUser, participant))
                .ownerId(currentUser.getUserName())
                .build();
    }

    private Conversation createMultiUserChat(List<User> participans, @Nullable String subject){
        Conversation conversation = new Conversation.Builder()
                .type(Conversation.Type.GROUP)
                .id(UUID.randomUUID().toString())
                .ownerId(currentUser.getUserName())
                .build();

        return setMultiUserChatData(conversation, participans, subject);
    }

    public Conversation modifyConversation(Conversation conversation, List<User> existParticipants,  List<User> newChatUsers, @Nullable String subject) {
        if (conversation.getType().equals(Conversation.Type.CHAT)) {
            conversation = new Conversation.Builder()
                    .ownerId(currentUser.getId())
                    .type(Conversation.Type.GROUP)
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
        MultiUserChat multiUserChat = messengerServerFacade.getChatManager()
                .createMultiUserChat(conversation.getId(), currentUser.getId(), true);

        multiUserChat.invite(newParticipants);
        if (!TextUtils.isEmpty(subject) && TextUtils.getTrimmedLength(subject) > 0) {
            multiUserChat.setSubject(subject);
            conversation.setSubject(subject);
        }

        return conversation;
    }
}
