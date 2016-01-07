package com.messenger.messengerservers;

import com.messenger.messengerservers.entities.User;
import com.messenger.messengerservers.listeners.AuthorizeListener;
import com.messenger.messengerservers.listeners.ConnectionListener;

public interface MessengerServerFacade {

    // TODO: 11/24/15 rename to connect
    void authorizeAsync(String username, String password);

    void disconnectAsync();

    boolean isAuthorized();

    void setPresenceStatus(boolean active);

    boolean isActive();

    void addAuthorizationListener(AuthorizeListener listener);

    void removeAuthorizationListener(AuthorizeListener listener);

    void addConnectionListener(ConnectionListener listener);

    void removeConnectionListener(ConnectionListener listener);

    ChatManager getChatManager();

    LoaderManager getLoaderManager();

    PaginationManager getPaginationManager();

    ContactManager getContactManager();

    GlobalEventEmitter getGlobalEventEmitter();

    User getOwner();

}
