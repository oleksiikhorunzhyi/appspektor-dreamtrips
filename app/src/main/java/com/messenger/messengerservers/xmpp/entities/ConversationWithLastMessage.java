package com.messenger.messengerservers.xmpp.entities;

import com.messenger.messengerservers.entities.Conversation;
import com.messenger.messengerservers.entities.Message;

public class ConversationWithLastMessage {
    public final Conversation conversation;
    public final Message lastMessage;

    public ConversationWithLastMessage(Conversation conversation, Message lastMessage) {
        this.conversation = conversation;
        this.lastMessage = lastMessage;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        ConversationWithLastMessage that = (ConversationWithLastMessage) o;

        return conversation != null ? conversation.equals(that.conversation) : that.conversation == null;

    }

    @Override
    public int hashCode() {
        return conversation != null ? conversation.hashCode() : 0;
    }

    @Override
    public String toString() {
        return "ConversationWithLastMessage{" +
                "conversation=" + conversation +
                ", lastMessage=" + lastMessage +
                '}';
    }
}
