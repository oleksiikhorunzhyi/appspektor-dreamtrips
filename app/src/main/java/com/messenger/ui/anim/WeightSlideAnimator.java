package com.messenger.ui.anim;

import android.animation.Animator;
import android.animation.ObjectAnimator;
import android.view.View;
import android.widget.LinearLayout;

public class WeightSlideAnimator {

   private final View view;
   private final ObjectAnimator slideInAnimator;
   private final ObjectAnimator slideOutAnimator;

   public WeightSlideAnimator(View view) {
      this.view = view;
      this.slideInAnimator = ObjectAnimator.ofFloat(new WeightWrapper(view), "weight", 1f, 0f);
      this.slideOutAnimator = ObjectAnimator.ofFloat(new WeightWrapper(view), "weight", 0f, 1f);
   }

   public void slideIn() {
      if ((slideInAnimator != null && slideInAnimator.isStarted()) || view.getVisibility() == View.VISIBLE) {
         return;
      }
      view.setVisibility(View.VISIBLE);
      slideInAnimator.start();
   }

   public void slideOut() {
      if ((slideOutAnimator != null && slideOutAnimator.isStarted()) || view.getVisibility() == View.GONE) {
         return;
      }
      slideOutAnimator.addListener(new SimpleAnimatorListener() {
         @Override
         public void onAnimationEnd(Animator animator) {
            view.setVisibility(View.GONE);
         }
      });
      slideOutAnimator.start();
   }

   private class WeightWrapper {
      private View view;

      public WeightWrapper(View view) {
         if (view.getLayoutParams() instanceof LinearLayout.LayoutParams) {
            this.view = view;
         } else {
            throw new IllegalArgumentException("The view should have LinearLayout as parent");
         }
      }

      public void setWeight(float weight) {
         LinearLayout.LayoutParams params = (LinearLayout.LayoutParams) view.getLayoutParams();
         params.weight = weight;
         view.setLayoutParams(params);
      }

      public float getWeight() {
         return ((LinearLayout.LayoutParams) view.getLayoutParams()).weight;
      }
   }
}
