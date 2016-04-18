package com.messenger.messengerservers;

import com.messenger.messengerservers.listeners.AuthorizeListener;
import com.messenger.messengerservers.listeners.ConnectionListener;

public interface MessengerServerFacade {

    void authorizeAsync(String username, String password);

    void disconnectAsync();

    boolean isAuthorized();

    boolean sendInitialPresence();

    boolean isActive();

    void setActive(boolean active);

    void addAuthorizationListener(AuthorizeListener listener);

    void removeAuthorizationListener(AuthorizeListener listener);

    void addConnectionListener(ConnectionListener listener);

    void removeConnectionListener(ConnectionListener listener);

    ChatManager getChatManager();

    LoaderManager getLoaderManager();

    PaginationManager getPaginationManager();

    GlobalEventEmitter getGlobalEventEmitter();

    String getUsername();

}
