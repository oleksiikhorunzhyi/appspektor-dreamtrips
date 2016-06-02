package com.messenger.ui.helper;

import android.text.TextUtils;

import com.innahema.collections.query.queriables.Queryable;
import com.messenger.entities.DataConversation;
import com.messenger.entities.DataUser;
import com.messenger.messengerservers.constant.ConversationStatus;
import com.messenger.messengerservers.constant.ConversationType;
import com.messenger.messengerservers.model.Conversation;
import com.messenger.messengerservers.model.MessengerUser;

import java.util.ArrayList;
import java.util.List;

public final class ConversationHelper {

    private ConversationHelper() {
    }

    /**
     * return companion's name if the given conversation is single
     * or custom subject if the conversation is group and conversation's subject has been set
     * or default value contains companion's name if the conversation is group and and conversation's subject hasn't been set
     */
    public static String obtainConversationSubject(DataConversation conversation, List<DataUser> participants) {
        switch (conversation.getType()) {
            case ConversationType.CHAT:
                return participants.get(0).getName();
            case ConversationType.GROUP:
            default:
                return !TextUtils.isEmpty(conversation.getSubject()) ?
                        conversation.getSubject() : obtainDefaultGroupChatSubject(participants);
        }
    }

    public static List<MessengerUser> getUsersFromConversations(List<Conversation> conversations) {
        List<MessengerUser> messengerUsers = new ArrayList<>();
        Queryable.from(conversations)
                .map(ConversationHelper::getUsersFromConversation)
                .forEachR(messengerUsers::addAll);
        return Queryable.from(messengerUsers).distinct().toList();
    }

    public static List<MessengerUser> getUsersFromConversation(Conversation conversation) {
        return Queryable.from(conversation.getParticipants())
                .map(participant -> new MessengerUser(participant.getUserId())).toList();
    }

    private static String obtainDefaultGroupChatSubject(List<DataUser> members) {
        return Queryable.from(members).map(DataUser::getFirstName).joinStrings(", ");
    }

    public static boolean isGroup(DataConversation conversation) {
        return conversation.getType() != null && !conversation.getType().equals(ConversationType.CHAT);
    }

    public static boolean isSingleChat(DataConversation conversation) {
        return ConversationType.CHAT.equals(conversation.getType());
    }

    public static boolean isTripChat(DataConversation conversation) {
        return TextUtils.equals(conversation.getType(), ConversationType.TRIP);
    }

    public static boolean isOwner(DataConversation conversation, DataUser user) {
        return conversation.getOwnerId() != null && conversation.getOwnerId().equals(user.getId());
    }

    public static boolean isAbandoned(DataConversation conversation) {
        return !ConversationStatus.PRESENT.equals(conversation.getStatus());
    }
}
