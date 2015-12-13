package com.messenger.messengerservers;

import com.messenger.messengerservers.listeners.AuthorizeListener;

public interface MessengerServerFacade {

    // TODO: 11/24/15 rename to connect
    void authorizeAsync(String username, String password);

    void disconnectAsync();

    boolean isAuthorized();

    void setPresenceStatus(boolean active);

    void addAuthorizationListener(AuthorizeListener listener);

    void removeAuthorizationListener(AuthorizeListener listener);

    ChatManager getChatManager();

    LoaderManager getLoaderManager();

    PaginationManager getPaginationManager();

    ContactManager getContactManager();

    GlobalEventEmitter getGlobalEventEmitter();

}
