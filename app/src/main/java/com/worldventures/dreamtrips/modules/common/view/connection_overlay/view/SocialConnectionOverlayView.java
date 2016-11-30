package com.worldventures.dreamtrips.modules.common.view.connection_overlay.view;

import rx.Observable;

public interface SocialConnectionOverlayView extends ConnectionOverlayView {
   void show();

   void hide();

   Observable<Void> getCloseClickObservable();
}
