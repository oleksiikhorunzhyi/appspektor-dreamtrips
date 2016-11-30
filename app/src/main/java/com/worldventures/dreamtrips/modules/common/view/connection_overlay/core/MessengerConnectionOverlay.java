package com.worldventures.dreamtrips.modules.common.view.connection_overlay.core;

import com.worldventures.dreamtrips.modules.common.view.connection_overlay.ConnectionState;
import com.worldventures.dreamtrips.modules.common.view.connection_overlay.view.MessengerConnectionOverlayView;
import com.worldventures.dreamtrips.modules.common.view.connection_overlay.view.MessengerConnectionOverlayViewFactory;

import rx.Observable;

public class MessengerConnectionOverlay extends ConnectionOverlay<MessengerConnectionOverlayView> {

   public MessengerConnectionOverlay(MessengerConnectionOverlayViewFactory connectionOverlayViewFactory) {
      super(connectionOverlayViewFactory);
   }

   @Override
   protected void connectionStateChanged(ConnectionState connectionState) {
      overlayView.setConnectionState(connectionState);
   }

   public Observable getRetryObservable() {
      return overlayView.getRetryObservable();
   }
}
