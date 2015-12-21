package com.messenger.messengerservers.entities;

import java.util.List;

public class ConversationWithParticipants {
    public final Message lastMessage;
    public final Conversation conversation;
    public final List<User> participants;

    public ConversationWithParticipants(Message lastMessage, Conversation conversation, List<User> participants) {
        this.lastMessage = lastMessage;
        this.conversation = conversation;
        this.participants = participants;
    }
}
