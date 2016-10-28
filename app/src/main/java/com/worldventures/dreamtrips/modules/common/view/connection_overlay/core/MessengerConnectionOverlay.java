package com.worldventures.dreamtrips.modules.common.view.connection_overlay.core;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;

import com.messenger.synchmechanism.SyncStatus;
import com.worldventures.dreamtrips.modules.common.view.connection_overlay.provider.MessengerConnectionStateProvider;
import com.worldventures.dreamtrips.modules.common.view.connection_overlay.view.MessengerConnectionOverlayView;

import rx.Observable;

public class MessengerConnectionOverlay extends ConnectionOverlay<MessengerConnectionStateProvider, MessengerConnectionOverlayView> {

   public MessengerConnectionOverlay(Context context, View rootView) {
      super(context, rootView, new MessengerConnectionStateProvider());
   }

   @Override
   protected MessengerConnectionOverlayView onCreateView(ViewGroup contentLayout) {
      return new MessengerConnectionOverlayView(context, contentLayout);
   }

   public void reportConnectionState(SyncStatus syncStatus) {
      connectionStateProvider.reportConnectionState(syncStatus);
   }

   public Observable getClickObservable() {
      return overlayView.getClickObservable();
   }
}
