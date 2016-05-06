package com.messenger.messengerservers;


import com.messenger.messengerservers.loaders.ContactsLoader;
import com.messenger.messengerservers.loaders.ConversationLoader;
import com.messenger.messengerservers.loaders.ConversationsLoader;

public interface LoaderManager {

    ContactsLoader createContactLoader();

    ConversationLoader createConversationLoader(String conversationId);

    ConversationsLoader createConversationsLoader();
}
