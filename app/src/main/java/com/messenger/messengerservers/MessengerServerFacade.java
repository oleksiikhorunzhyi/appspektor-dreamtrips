package com.messenger.messengerservers;

import com.messenger.messengerservers.chat.ChatManager;

import rx.Observable;

public interface MessengerServerFacade {

   void connect(String username, String password);

   void disconnect();

   boolean isConnected();

   boolean sendInitialPresence();

   boolean isActive();

   void setActive(boolean active);

   ChatManager getChatManager();

   LoaderManager getLoaderManager();

   PaginationManager getPaginationManager();

   GlobalEventEmitter getGlobalEventEmitter();

   String getUsername();

   Observable<ConnectionStatus> getStatusObservable();

   ChatExtensions getChatExtensions();
}
