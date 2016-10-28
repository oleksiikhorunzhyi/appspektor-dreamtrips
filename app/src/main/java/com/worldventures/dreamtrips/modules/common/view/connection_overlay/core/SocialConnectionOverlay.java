package com.worldventures.dreamtrips.modules.common.view.connection_overlay.core;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;

import com.worldventures.dreamtrips.modules.common.view.connection_overlay.provider.ReactiveNetworkConnectionStateProvider;
import com.worldventures.dreamtrips.modules.common.view.connection_overlay.view.SocialConnectionOverlayView;

import rx.Observable;

public class SocialConnectionOverlay extends ConnectionOverlay<ReactiveNetworkConnectionStateProvider, SocialConnectionOverlayView> {

   public SocialConnectionOverlay(Context context, View rootView) {
      super(context, rootView, new ReactiveNetworkConnectionStateProvider(context));
   }

   @Override
   protected SocialConnectionOverlayView onCreateView(ViewGroup contentLayout) {
      return new SocialConnectionOverlayView(context, contentLayout);
   }

   @Override
   public void startProcessingState(Observable stopper) {
      super.startProcessingState(stopper);
      connectionStateProvider.startMonitoringNetworkState(stopper);
      overlayView.getCloseClickObservable()
            .compose(bindToStopper(stopper))
            .subscribe(event -> {
               // todo temporary disabled
               // hide();
            });
   }

   public void show() {
      if (!hasContentLayout()) return;
      overlayView.show();
   }

   public void hide() {
      if (!hasContentLayout()) return;
      overlayView.hide();
   }
}
