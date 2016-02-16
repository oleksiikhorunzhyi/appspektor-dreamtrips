package com.messenger.messengerservers;


import com.messenger.messengerservers.loaders.Loader;
import com.messenger.messengerservers.loaders.ParticipantsLoader;
import com.messenger.messengerservers.model.Conversation;
import com.messenger.messengerservers.model.User;

public interface LoaderManager {

    Loader<User> createContactLoader();

    Loader<Conversation> createConversationLoader();

    ParticipantsLoader createParticipantsLoader();
}
