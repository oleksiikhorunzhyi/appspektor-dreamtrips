package com.worldventures.dreamtrips.modules.common.view.connection_overlay.view;

import android.content.Context;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.worldventures.dreamtrips.R;
import com.worldventures.dreamtrips.modules.common.view.connection_overlay.ConnectionState;

import rx.Observable;
import rx.subjects.PublishSubject;

public class MessengerConnectionOverlayView implements ConnectionOverlayView {

   private final Context context;

   private final ViewGroup parentView;
   private View overlayView;
   private View disconnectedView;
   private View connectingView;

   private final PublishSubject retryObservable = PublishSubject.create();

   public MessengerConnectionOverlayView(Context context, @Nullable ViewGroup contentView) {
      this.context = context;
      this.parentView = contentView;
   }

   public Observable getRetryObservable() {
      return retryObservable;
   }

   public void setConnectionState(ConnectionState connectionState) {
      if (parentView == null) {
         return;
      }

      attachOverlayViewIfNeeded();

      switch (connectionState) {
         case CONNECTED:
            overlayView.setVisibility(View.GONE);
            break;
         case CONNECTING:
            overlayView.setVisibility(View.VISIBLE);
            connectingView.setVisibility(View.VISIBLE);
            disconnectedView.setVisibility(View.GONE);
            break;
         case DISCONNECTED:
            overlayView.setVisibility(View.VISIBLE);
            disconnectedView.setVisibility(View.VISIBLE);
            connectingView.setVisibility(View.GONE);
            break;
         default:
            break;
      }
   }

   private void attachOverlayViewIfNeeded() {
      if (overlayView != null) {
         return;
      }
      overlayView = LayoutInflater.from(context)
            .inflate(R.layout.layout_connection_overlay, parentView, false);
      overlayView.findViewById(R.id.connection_overlay_reconnect_button)
            .setOnClickListener(v -> retryObservable.onNext(null));
      disconnectedView = overlayView.findViewById(R.id.connection_overlay_disconnected_view);
      connectingView = overlayView.findViewById(R.id.connection_overlay_connecting_view);
      parentView.addView(overlayView);
      overlayView.setVisibility(View.GONE);
   }
}
