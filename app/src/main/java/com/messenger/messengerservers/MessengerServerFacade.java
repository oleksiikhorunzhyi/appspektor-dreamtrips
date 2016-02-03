package com.messenger.messengerservers;

import com.messenger.messengerservers.listeners.AuthorizeListener;
import com.messenger.messengerservers.listeners.ConnectionListener;
import com.messenger.messengerservers.model.User;

public interface MessengerServerFacade {

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

    String getUsername();

}
