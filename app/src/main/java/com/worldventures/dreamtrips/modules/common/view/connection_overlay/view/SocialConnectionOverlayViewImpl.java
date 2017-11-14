package com.worldventures.dreamtrips.modules.common.view.connection_overlay.view;

import android.content.Context;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.worldventures.dreamtrips.R;

import rx.Observable;
import rx.subjects.PublishSubject;

public class SocialConnectionOverlayViewImpl implements SocialConnectionOverlayView {

   private final Context context;
   private final ViewGroup parentView;
   private final PublishSubject<Void> closeClickObservable = PublishSubject.create();
   private View overlayView;

   public SocialConnectionOverlayViewImpl(Context context, @Nullable ViewGroup parentView) {
      this.context = context;
      this.parentView = parentView;
   }

   private boolean attachOverlayIfNeeded() {
      if (parentView == null) {
         return false;
      }
      if (overlayView != null) {
         return true;
      }
      overlayView = LayoutInflater.from(context)
            .inflate(R.layout.view_social_offline_indicator, parentView, false);
      overlayView.findViewById(R.id.offline_indicator_close)
            .setOnClickListener(v -> closeClickObservable.onNext(null));
      overlayView.setOnClickListener(v -> {
      });
      parentView.addView(overlayView);
      return true;
   }

   @Override
   public void show() {
      if (attachOverlayIfNeeded()) {
         overlayView.setVisibility(View.VISIBLE);
      }
   }

   @Override
   public void hide() {
      if (attachOverlayIfNeeded()) {
         overlayView.setVisibility(View.GONE);
      }
   }

   @Override
   public Observable<Void> getCloseClickObservable() {
      return closeClickObservable;
   }
}
