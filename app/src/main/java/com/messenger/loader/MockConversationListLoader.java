package com.messenger.loader;

import com.messenger.model.ChatConversation;

import java.util.ArrayList;
import java.util.List;

public class MockConversationListLoader extends SimpleLoader<List<ChatConversation>> {

    public void loadData(final LoadListener<List<ChatConversation>> listener) {
        MockLoader<List<ChatConversation>> chatConversationMockLoader = new MockLoader<List<ChatConversation>>() {
            @Override public List<ChatConversation> provideData() {
                return MockConversationListLoader.this.provideData();
            }
        };
        chatConversationMockLoader.loadData(listener);
    }

    @Override public List<ChatConversation> provideData() {
        List<ChatConversation> conversations = new ArrayList<>();
        conversations.add(MockChatConversationLoader.newChatConversation("Conversation 1", 2, 10, 1));
        conversations.add(MockChatConversationLoader.newChatConversation("Conversation 2", 4, 8, 1));
        conversations.add(MockChatConversationLoader.newChatConversation("Conversation 3", 3, 7, 1));
        conversations.add(MockChatConversationLoader.newChatConversation("Conversation 4", 3, 2, 0));
        conversations.add(MockChatConversationLoader.newChatConversation("Conversation 5", 2, 3, 0));
        conversations.add(MockChatConversationLoader.newChatConversation("Conversation 6", 7, 15, 0));
        return conversations;
    }

}
