package com.worldventures.dreamtrips.modules.common.view.connection_overlay.core;

import com.worldventures.dreamtrips.modules.common.view.connection_overlay.ConnectionState;
import com.worldventures.dreamtrips.modules.common.view.connection_overlay.view.ConnectionOverlayView;
import com.worldventures.dreamtrips.modules.common.view.connection_overlay.view.ConnectionOverlayViewFactory;

import rx.Observable;

public abstract class ConnectionOverlay<V extends ConnectionOverlayView> {

   protected V overlayView;

   ConnectionOverlay(ConnectionOverlayViewFactory<V> connectionOverlayViewFactory) {
      this.overlayView = connectionOverlayViewFactory.createOverlayView();
   }

   public void startProcessingState(Observable<ConnectionState> connectionStateObservable, Observable<Void> stopper) {
      if (!hasContentLayout()) return;
      processStateInternally(connectionStateObservable, stopper);
   }

   protected void processStateInternally(Observable<ConnectionState> connectionStateObservable, Observable<Void> stopper) {
      connectionStateObservable
            .compose(bindToStopper(stopper))
            .subscribe(this::connectionStateChanged);
   }

   protected abstract void connectionStateChanged(ConnectionState connectionState);

   private boolean hasContentLayout() {
      return overlayView != null;
   }

   <T> Observable.Transformer<T, T> bindToStopper(Observable stopper) {
      return input -> input.takeUntil(stopper);
   }
}
