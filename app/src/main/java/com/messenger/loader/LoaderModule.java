package com.messenger.loader;

import com.messenger.app.Environment;
import com.messenger.model.ChatConversation;
import com.messenger.model.ChatUser;

import java.util.List;

public abstract class LoaderModule {

    public static SimpleLoader<List<ChatUser>> getChatContactsLoader() {
        switch (Environment.getEnvironment()) {
//            case Environment.PROD:
//                return ;
            case Environment.MOCK:
            default:
                return new MockChatContactsLoader();

        }
    }

    public static SimpleLoader<ChatConversation> getChatConversationLoader(ChatConversation chatConversation) {
        switch (Environment.getEnvironment()) {
//            case Environment.PROD:
//                return ;
            case Environment.MOCK:
            default:
                return new MockChatConversationLoader(chatConversation);
        }
    }

    public static SimpleLoader<List<ChatConversation>> getConversationListLoader() {
        switch (Environment.getEnvironment()) {
            case Environment.MOCK:
            default:
                return new MockConversationListLoader();
        }
    }
}
