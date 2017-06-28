package com.worldventures.dreamtrips.wallet.ui.common.helper2.progress;

import android.animation.ObjectAnimator;
import android.annotation.TargetApi;
import android.os.Build;

import io.techery.janet.operationsubscriber.view.ProgressView;

public class AnimatorProgressView<T> implements ProgressView<T> {

   private final ProgressView progressViewImpl;

   public AnimatorProgressView(ObjectAnimator animator) {
      animator.setRepeatCount(ObjectAnimator.INFINITE);
      if (Build.VERSION.SDK_INT < Build.VERSION_CODES.KITKAT) {
         progressViewImpl = new PreKitKatImpl();
      } else {
         progressViewImpl = new KitKatImpl(animator);
      }
   }

   @Override
   public void showProgress(T t) {
      //noinspection unchecked
      progressViewImpl.showProgress(t);
   }

   @Override
   public boolean isProgressVisible() {
      return progressViewImpl.isProgressVisible();
   }

   @Override
   public void hideProgress() {
      progressViewImpl.hideProgress();
   }

   private static class PreKitKatImpl implements ProgressView {
      private boolean started;

      @Override
      public void showProgress(Object o) {
         started = true;
      }

      @Override
      public boolean isProgressVisible() {
         return started;
      }

      @Override
      public void hideProgress() {
         started = false;
      }
   }

   @TargetApi(Build.VERSION_CODES.KITKAT)
   private static class KitKatImpl implements ProgressView {

      private final ObjectAnimator animator;

      private KitKatImpl(ObjectAnimator animator) {
         this.animator = animator;
      }

      @Override
      public void showProgress(Object t) {
         animator.start();
      }

      @Override
      public boolean isProgressVisible() {
         return animator.isStarted();
      }

      @Override
      public void hideProgress() {
         animator.pause();
      }
   }
}
