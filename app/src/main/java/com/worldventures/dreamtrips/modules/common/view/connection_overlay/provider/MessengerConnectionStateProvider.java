package com.worldventures.dreamtrips.modules.common.view.connection_overlay.provider;

import com.messenger.synchmechanism.SyncStatus;
import com.worldventures.dreamtrips.modules.common.view.connection_overlay.ConnectionState;

import rx.Observable;
import rx.subjects.PublishSubject;

public class MessengerConnectionStateProvider implements ConnectionStateProvider<SyncStatus> {
   private PublishSubject<ConnectionState> connectionStatusObservable = PublishSubject.create();

   @Override
   public void reportConnectionState(SyncStatus state) {
      connectionStatusObservable.onNext(mapStatus(state));
   }

   @Override
   public Observable<ConnectionState> connectionStateObservable() {
      return connectionStatusObservable;
   }

   protected ConnectionState mapStatus(SyncStatus status) {
      switch (status) {
         case CONNECTED:
            return ConnectionState.CONNECTED;
         case SYNC_DATA:
         case CONNECTING:
            return ConnectionState.CONNECTING;
         case ERROR:
         case DISCONNECTED:
            return ConnectionState.DISCONNECTED;
         default:
            throw new IllegalArgumentException("No such status");
      }
   }
}
