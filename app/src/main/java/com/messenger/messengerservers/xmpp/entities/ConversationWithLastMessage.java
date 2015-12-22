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
}
