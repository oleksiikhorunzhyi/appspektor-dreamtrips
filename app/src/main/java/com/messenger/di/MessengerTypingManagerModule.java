package com.messenger.di;

import com.messenger.delegate.chat.typing.MemoryTypingStore;
import com.messenger.delegate.chat.typing.TypingManager;
import com.messenger.messengerservers.MessengerServerFacade;
import com.messenger.storage.dao.UsersDAO;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;

@Module(
        complete = false,
        library = true
)
public class MessengerTypingManagerModule {

    @Provides
    @Singleton
    TypingManager provideTypingManager(MessengerServerFacade serverFacade, UsersDAO usersDAO) {
        return new TypingManager(serverFacade, usersDAO, new MemoryTypingStore());
    }
}
