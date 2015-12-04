package com.messenger.loader;

import java.util.ArrayList;
import java.util.Calendar;

import com.messenger.app.Environment;
import com.messenger.model.ChatContacts;
import com.messenger.model.ChatConversation;
import com.messenger.model.ChatMessage;
import com.messenger.model.ChatUser;
import com.messenger.model.MockChatConversation;

public class MockChatConversationLoader extends ConversationLoader {

    public MockChatConversationLoader(ChatConversation chatConversation) {
        super(chatConversation);
    }

    public void loadData(final LoadListener<ChatConversation> listener) {
        MockLoader<ChatConversation> chatConversationMockLoader = new MockLoader<ChatConversation>() {
            @Override public ChatConversation provideData() {
                return MockChatConversationLoader.this.provideData();
            }
        };
        chatConversationMockLoader.loadData(listener);
    }

    @Override public ChatConversation provideData() {
        // Create new conversation from argument conversation
        ChatConversation chatConversation = new MockChatConversation();
        chatConversation.setChatUsers(getChatConversation().getChatUsers());
        chatConversation.setConversationOwner(getChatConversation().getConversationOwner());
        chatConversation.setConversationName(getChatConversation().getConversationName());

        final ChatUser chatOwner = chatConversation.getConversationOwner();

        ArrayList<ChatMessage> chatMessages = new ArrayList<>();
        long now = System.currentTimeMillis();
        long firstMessage = now - 24 * 3600 * 100; // one day ago
        long timeRange = now - firstMessage;
        final int messagesCount = 4;

        ChatContacts mockChatContacts = new MockChatContactsLoader().provideData();

        for (int i = 0; i < messagesCount; i++) {
            ChatUser chatUser = i % 2 == 0 ? chatOwner : mockChatContacts.getUsers().get(i);
            String message = "Message " + String.valueOf(i + 1);
            long timeStamp = firstMessage + timeRange / messagesCount * i;
            Calendar calendar = Calendar.getInstance();
            calendar.setTimeInMillis(timeStamp);
            ChatMessage chatMessage = Environment.newChatMessage();
            chatMessage.setMessage(message);
            chatMessage.setUser(chatUser);
            chatMessage.setDate(calendar.getTime());
            chatMessages.add(chatMessage);
        }
        chatConversation.setChatMessages(chatMessages);

        return chatConversation;
    }
}
