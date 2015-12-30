package com.messenger.messengerservers;


import com.messenger.messengerservers.entities.ConversationData;
import com.messenger.messengerservers.entities.User;
import com.messenger.messengerservers.loaders.Loader;

public interface LoaderManager {

    Loader<User> createContactLoader();

    Loader<ConversationData> createConversationLoader();
}
