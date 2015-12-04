package com.messenger.messengerservers;

import com.messenger.messengerservers.chat.MultiUserChat;
import com.messenger.messengerservers.chat.SingleUserChat;
import com.messenger.messengerservers.entities.User;
import com.messenger.messengerservers.listeners.AuthorizeListener;
import com.messenger.messengerservers.listeners.GlobalMessageReceiver;

public interface MessengerServerFacade {

    // TODO: 11/24/15 rename to connect
    void authorizeAsync(String username, String password);

    void disconnectAsync();

    boolean isAuthorized();

    void addAuthorizationListener(AuthorizeListener listener);

    void removeAuthorizationListener(AuthorizeListener listener);

    SingleUserChat createSingleUserChat(User companion);

    MultiUserChat createMultiUserChat(User owner);

    LoaderManager getLoaderManager();

    PaginationManager getPaginatorManager();

    GlobalEventEmitter getGlobalEventEmitter();

    void setGlobalMessageReceiver(GlobalMessageReceiver globalMessageReceiver);

    void setPresenceStatus(boolean active);

}
