package com.worldventures.dreamtrips.modules.common.view.connection_overlay.core;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;

import com.worldventures.dreamtrips.modules.common.view.connection_overlay.view.MessengerConnectionOverlayView;

import rx.Observable;

public class MessengerConnectionOverlay extends ConnectionOverlay<MessengerConnectionOverlayView> {

   public MessengerConnectionOverlay(Context context, View rootView) {
      super(context, rootView);
   }

   @Override
   protected MessengerConnectionOverlayView onCreateView(ViewGroup contentLayout) {
      return new MessengerConnectionOverlayView(context, contentLayout);
   }

   public Observable getRetryObservable() {
      return overlayView.getRetryObservable();
   }
}
