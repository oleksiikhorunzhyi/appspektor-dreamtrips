package com.worldventures.dreamtrips.modules.common.view.connection_overlay.core;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;

import com.worldventures.dreamtrips.modules.common.view.connection_overlay.ConnectionState;
import com.worldventures.dreamtrips.modules.common.view.connection_overlay.view.SocialConnectionOverlayView;

import rx.Observable;

public class SocialConnectionOverlay extends ConnectionOverlay<SocialConnectionOverlayView> {

   public SocialConnectionOverlay(Context context, View rootView) {
      super(context, rootView);
   }

   public SocialConnectionOverlay(Context context, View rootView, int contentLayoutId) {
      super(context, rootView, contentLayoutId);
   }

   @Override
   protected SocialConnectionOverlayView onCreateView(ViewGroup contentLayout) {
      return new SocialConnectionOverlayView(context, contentLayout);
   }

   @Override
   protected void processStateInternally(Observable<ConnectionState> connectionStateObservable,
         Observable stopper) {
      super.processStateInternally(connectionStateObservable, stopper);
      overlayView.getCloseClickObservable()
            .compose(bindToStopper(stopper))
            .subscribe(event -> overlayView.hide());
   }
}
