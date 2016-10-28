package com.worldventures.dreamtrips.modules.common.view.connection_overlay.core;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;

import com.worldventures.dreamtrips.R;
import com.worldventures.dreamtrips.modules.common.view.connection_overlay.ConnectionState;
import com.worldventures.dreamtrips.modules.common.view.connection_overlay.provider.ConnectionStateProvider;
import com.worldventures.dreamtrips.modules.common.view.connection_overlay.view.ConnectionOverlayView;

import rx.Observable;

public abstract class ConnectionOverlay<P extends ConnectionStateProvider, V extends ConnectionOverlayView> {

   protected Context context;
   protected View rootView;
   protected P connectionStateProvider;
   protected V overlayView;

   public ConnectionOverlay(Context context, View rootView, P connectionStateProvider) {
      this.context = context;
      this.rootView = rootView;
      this.connectionStateProvider = connectionStateProvider;
      ViewGroup contentLayout = findContentLayout();
      if (contentLayout != null) {
         this.overlayView = onCreateView(contentLayout);
      }
   }

   protected abstract V onCreateView(ViewGroup contentLayout);

   protected boolean hasContentLayout() {
      return findContentLayout() != null;
   }

   protected ViewGroup findContentLayout() {
      return (ViewGroup) rootView.findViewById(R.id.content_layout);
   }

   public void startProcessingState(Observable stopper) {
      if (!hasContentLayout()) return;
      connectionStateProvider.connectionStateObservable()
            .compose(bindToStopper(stopper))
            .subscribe(state -> {
               overlayView.setConnectionState((ConnectionState) state);
            });
   }

   protected <T> Observable.Transformer<T, T> bindToStopper(Observable stopper) {
      return input -> input.takeUntil(stopper);
   }
}
