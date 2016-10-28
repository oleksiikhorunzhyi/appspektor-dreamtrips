package com.worldventures.dreamtrips.modules.common.view.custom;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.worldventures.dreamtrips.R;

import rx.Observable;
import rx.subjects.PublishSubject;

public class DisconnectedOverlay {

   enum State {
      CONNECTING,
      DISCONNECTED
   }

   private Context context;

   private ViewGroup parentView;
   private View overlayView;
   private View disconnectedView;
   private View connectingView;

   private PublishSubject clickObservable = PublishSubject.create();

   public DisconnectedOverlay(Context context, ViewGroup contentView) {
      this.context = context;
      this.parentView = contentView;
   }

   public Observable getClickObservable() {
      return clickObservable;
   }

   public void showConnecting() {
      show(State.CONNECTING);
   }

   public void showDisconnected() {
      show(State.DISCONNECTED);
   }

   private void show(State state) {
      attachDisconnectedOverlayIfNeeded();

      switch (state) {
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
      }
   }

   private void attachDisconnectedOverlayIfNeeded() {
      if (overlayView != null) {
         return;
      }
      overlayView = LayoutInflater.from(context)
            .inflate(R.layout.layout_disconnected_overlay, parentView, false);
      overlayView.findViewById(R.id.disconnected_overlay_reconnect_button)
            .setOnClickListener(v -> clickObservable.onNext(null));
      disconnectedView = overlayView.findViewById(R.id.disconnected_overlay_disconnected_view);
      connectingView = overlayView.findViewById(R.id.disconnected_overlay_connecting_view);
      parentView.addView(overlayView);
      overlayView.setVisibility(View.GONE);
   }

   public void hide() {
      overlayView.setVisibility(View.GONE);
   }
}
