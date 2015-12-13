package com.messenger.loader;

import com.messenger.app.Environment;
import com.messenger.model.ChatConversation;
import com.messenger.model.ChatMessage;
import com.messenger.model.ChatUser;
import com.messenger.model.MockChatConversation;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

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
        // create new conversation from argument conversation
        ChatConversation chatConversation = new MockChatConversation();
        chatConversation.setChatUsers(getChatConversation().getChatUsers());
        chatConversation.setConversationOwner(getChatConversation().getConversationOwner());
        chatConversation.setConversationName(getChatConversation().getConversationName());

        if (getChatConversation().getMessages() == null ||getChatConversation().getMessages().size() == 0) {
            loadConversationWithMessagesAndTypingCount(chatConversation, 6, 3);
        } else {
            chatConversation.setChatMessages(getChatConversation().getMessages());
            chatConversation.setTypingUsers(getChatConversation().getTypingUsers());
        }

        return chatConversation;
    }

    public static void loadConversationWithMessagesAndTypingCount(ChatConversation chatConversation,
            int messagesCount, int typingCount) {
        ArrayList<ChatMessage> chatMessages = new ArrayList<>();
        List<ChatUser> chatUsers = chatConversation.getChatUsers();
        ChatUser conversationOwner = chatConversation.getConversationOwner();

        long now = System.currentTimeMillis();
        final long oneDay = 24 * 3600 * 1000;
        long firstMessage = now - 1 * oneDay;
        long timeRange = now - firstMessage;
        for (int i = 0; i < messagesCount; i++) {
            ChatUser chatUser;
            if (i % 4 == 0 || i % 4 == 1) {
                chatUser = conversationOwner;
            } else {
                if (chatConversation.isGroupConversation()) {
                    chatUser = chatUsers.get(i % chatUsers.size());
                } else {
                    // if there are only two users for testing purposes ensure we don't pick author again
                    chatUser = chatUsers.get(1);
                }
            }
            String message = "Message " + String.valueOf(i + 1);
            ChatMessage chatMessage = Environment.newChatMessage();
            chatMessage.setMessage(message);
            chatMessage.setUser(chatUser);
            long timeStamp = firstMessage + timeRange / messagesCount * i;
            Calendar calendar = Calendar.getInstance();
            calendar.setTimeInMillis(timeStamp);
            chatMessage.setDate(calendar.getTime());
            if (i == (messagesCount - 1)) {
                chatMessage.setUnread(true);
            }
            chatMessages.add(chatMessage);
        }
        chatConversation.setChatMessages(chatMessages);

        List<ChatUser> typingUsers = new ArrayList<>();
        typingCount = Math.min(typingCount, chatUsers.size());
        for (int i = 0; i < typingCount; i++) {
            typingUsers.add(chatUsers.get(i));
        }
        chatConversation.setTypingUsers(typingUsers);
    }

    public static ChatConversation newChatConversation(String name, int chatUsersCount, int messagesCount,
            int typingUsersCount) {
        ChatConversation chatConversation = Environment.newChatConversation();
        chatConversation.setConversationName(name);
        chatConversation.setConversationOwner(Environment.getCurrentUser());
        chatConversation.setChatUsers(MockChatContactsLoader.provideChatUsers(true, chatUsersCount));
        loadConversationWithMessagesAndTypingCount(chatConversation, messagesCount, typingUsersCount);
        return chatConversation;
    }
}
