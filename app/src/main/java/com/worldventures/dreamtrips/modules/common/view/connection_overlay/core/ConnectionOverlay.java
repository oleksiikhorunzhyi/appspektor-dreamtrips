package com.worldventures.dreamtrips.modules.common.view.connection_overlay.core;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;

import com.worldventures.dreamtrips.R;
import com.worldventures.dreamtrips.modules.common.view.connection_overlay.ConnectionState;
import com.worldventures.dreamtrips.modules.common.view.connection_overlay.view.ConnectionOverlayView;

import rx.Observable;

public abstract class ConnectionOverlay<V extends ConnectionOverlayView> {

   protected Context context;
   protected V overlayView;

   ConnectionOverlay(Context context, View rootView) {
      this(context, rootView, R.id.content_layout);
   }

   ConnectionOverlay(Context context, View rootView, int contentLayoutId) {
      this.context = context;
      ViewGroup contentLayout = findContentLayout(rootView, contentLayoutId);
      if (contentLayout != null) {
         this.overlayView = onCreateView(contentLayout);
      }
   }

   public void startProcessingState(Observable<ConnectionState> connectionStateObservable, Observable stopper) {
      if (!hasContentLayout()) return;
      processStateInternally(connectionStateObservable, stopper);
   }

   protected void processStateInternally(Observable<ConnectionState> connectionStateObservable, Observable stopper) {
      connectionStateObservable
            .compose(bindToStopper(stopper))
            .subscribe(overlayView::setConnectionState);
   }

   protected abstract V onCreateView(ViewGroup contentLayout);

   private boolean hasContentLayout() {
      return overlayView != null;
   }

   private ViewGroup findContentLayout(View rootView, int contentLayoutId) {
      return (ViewGroup) rootView.findViewById(contentLayoutId);
   }

   <T> Observable.Transformer<T, T> bindToStopper(Observable stopper) {
      return input -> input.takeUntil(stopper);
   }
}
