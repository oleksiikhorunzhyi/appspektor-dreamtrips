package com.messenger.loader;

import com.messenger.app.Environment;
import com.messenger.model.ChatContacts;
import com.messenger.model.ChatConversation;

public abstract class LoaderModule {

    public static SimpleLoader<ChatContacts> getChatContactsLoader() {
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
}
