package com.messenger.app;

import com.messenger.model.ChatConversation;
import com.messenger.model.ChatMessage;
import com.messenger.model.ChatUser;
import com.messenger.model.MockChatContacts;
import com.messenger.model.MockChatConversation;
import com.messenger.model.MockChatMessage;
import com.messenger.model.MockChatUser;

public class Environment {

    public static final int MOCK = 1;
    public static final int PROD = 2;

    public static int getEnvironment() {
        return MOCK;
    }

    public static ClassLoader getChatUserClassLoader() {
        switch (getEnvironment()) {
//            case Environment.PROD:
//                return ;
            case MOCK:
            default:
                return MockChatUser.class.getClassLoader();
        }
    }

    public static ClassLoader getChatContactsClassLoader() {
        switch (getEnvironment()) {
            case MOCK:
            default:
                return MockChatContacts.class.getClassLoader();
        }
    }

    public static ChatUser getCurrentUser() {
        switch (getEnvironment()) {
            case MOCK:
            default:
                return new MockChatUser("Name Surname " + String.valueOf(1),
                    "http://www.skivecore.com/members/0/Default.jpg");
        }
    }

    public static ChatConversation newChatConversation() {
        switch (Environment.getEnvironment()) {
            case Environment.MOCK:
            default:
                return new MockChatConversation();
        }
    }

    public static ChatMessage newChatMessage() {
        switch (Environment.getEnvironment()) {
            case Environment.MOCK:
            default:
                return new MockChatMessage();
        }
    }
}
