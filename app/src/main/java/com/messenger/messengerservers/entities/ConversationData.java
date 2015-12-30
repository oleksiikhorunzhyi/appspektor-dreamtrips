package com.messenger.messengerservers.entities;

import java.util.List;

public class ConversationData {
    public final Conversation conversation;
    public final List<User> participants;
    public final Message lastMessage;

    public ConversationData(Conversation conversation, List<User> participants, Message lastMessage) {
        this.lastMessage = lastMessage;
        this.conversation = conversation;
        this.participants = participants;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        ConversationData that = (ConversationData) o;

        return conversation != null ? conversation.equals(that.conversation) : that.conversation == null;

    }

    @Override
    public int hashCode() {
        return conversation != null ? conversation.hashCode() : 0;
    }

    @Override
    public String toString() {
        return "ConversationData{" +
                "conversation=" + conversation +
                ", participants=" + participants +
                ", lastMessage=" + lastMessage +
                '}';
    }
}
