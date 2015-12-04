package com.messenger.loader;

import com.messenger.model.ChatConversation;

public abstract class ConversationLoader extends SimpleLoader<ChatConversation> {

    private ChatConversation chatConversation;

    public ConversationLoader(ChatConversation chatConversation) {
        this.chatConversation = chatConversation;
    }

    protected ChatConversation getChatConversation() {
        return chatConversation;
    }
}
