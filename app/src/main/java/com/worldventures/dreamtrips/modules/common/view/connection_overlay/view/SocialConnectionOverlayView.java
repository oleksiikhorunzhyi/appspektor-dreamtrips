package com.worldventures.dreamtrips.modules.common.view.connection_overlay.view;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.worldventures.dreamtrips.R;
import com.worldventures.dreamtrips.modules.common.view.connection_overlay.ConnectionState;

import rx.Observable;
import rx.subjects.PublishSubject;
import rx.subjects.ReplaySubject;

public class SocialConnectionOverlayView implements ConnectionOverlayView {

   private Context context;
   private ViewGroup parentView;
   private View overlayView;

   private ReplaySubject closeClickObservable = ReplaySubject.create();

   public SocialConnectionOverlayView(Context context, ViewGroup parentView) {
      this.context = context;
      this.parentView = parentView;
   }

   @Override
   public void setConnectionState(ConnectionState connectionState) {
      switch (connectionState) {
         case DISCONNECTED:
         case CONNECTING:
            show();
            break;
         case CONNECTED:
            hide();
            break;
      }
   }

   private boolean attachOverlayIfNeeded() {
      if (parentView == null) return false;
      if (overlayView != null) return true;
      overlayView = LayoutInflater.from(context)
            .inflate(R.layout.view_social_offline_indicator, parentView, false);
      overlayView.findViewById(R.id.offline_indicator_close)
            .setOnClickListener(v -> closeClickObservable.onNext(null));
      overlayView.setOnClickListener(v -> {});
      parentView.addView(overlayView);
      return true;
   }

   public void show() {
      if (attachOverlayIfNeeded()) {
         overlayView.setVisibility(View.VISIBLE);
      }
   }

   public void hide() {
      if (attachOverlayIfNeeded()) {
         overlayView.setVisibility(View.GONE);
      }
   }

   public Observable getCloseClickObservable() {
      return closeClickObservable;
   }
}
