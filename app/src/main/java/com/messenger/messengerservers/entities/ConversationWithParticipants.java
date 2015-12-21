package com.messenger.messengerservers.entities;

import java.util.List;

public class ConversationWithParticipants {

    public final Conversation conversation;
    public final List<User> participants;

    public ConversationWithParticipants(Conversation conversation, List<User> participants) {
        this.conversation = conversation;
        this.participants = participants;
    }
}
