package com.worldventures.dreamtrips.modules.common.view.connection_overlay.provider;

import com.worldventures.dreamtrips.modules.common.view.connection_overlay.ConnectionState;

import rx.Observable;

public interface ConnectionStateProvider<T> {

   void reportConnectionState(T state);

   Observable<ConnectionState> connectionStateObservable();
}
