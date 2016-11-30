package com.worldventures.dreamtrips.modules.common.view.connection_overlay.core;

import com.worldventures.dreamtrips.modules.common.view.connection_overlay.ConnectionState;
import com.worldventures.dreamtrips.modules.common.view.connection_overlay.view.ConnectionOverlayViewFactory;
import com.worldventures.dreamtrips.modules.common.view.connection_overlay.view.SocialConnectionOverlayView;
import com.worldventures.dreamtrips.modules.common.view.connection_overlay.view.SocialConnectionOverlayViewFactory;

import rx.Observable;

public class SocialConnectionOverlay extends ConnectionOverlay<SocialConnectionOverlayView> {

   public SocialConnectionOverlay(SocialConnectionOverlayViewFactory connectionOverlayViewFactory) {
      super(connectionOverlayViewFactory);
   }

   @Override
   protected void connectionStateChanged(ConnectionState connectionState) {
      switch (connectionState) {
         case DISCONNECTED:
         case CONNECTING:
            overlayView.show();
            break;
         case CONNECTED:
            overlayView.hide();
            break;
      }
   }

   @Override
   protected void processStateInternally(Observable<ConnectionState> connectionStateObservable,
         Observable<Void> stopper) {
      super.processStateInternally(connectionStateObservable, stopper);
      overlayView.getCloseClickObservable()
            .compose(bindToStopper(stopper))
            .subscribe(event -> overlayView.hide());
   }
}
