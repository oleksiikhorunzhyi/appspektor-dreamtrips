package com.messenger.messengerservers;


import com.messenger.messengerservers.entities.ConversationWithParticipants;
import com.messenger.messengerservers.entities.User;
import com.messenger.messengerservers.loaders.Loader;

public interface LoaderManager {
    Loader<User> createContactLoader();

    Loader<ConversationWithParticipants> createConversationLoader();

    void destroyLoader(Loader loader);

    void close();
}
