package com.worldventures.dreamtrips.wallet.util;

import android.animation.Animator;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.support.annotation.NonNull;
import android.view.View;
import android.view.ViewGroup;

import com.bluelinelabs.conductor.changehandler.AnimatorChangeHandler;

public class RevertHorizontalChangeHandler extends AnimatorChangeHandler {
   public RevertHorizontalChangeHandler() {
   }

   public RevertHorizontalChangeHandler(boolean removesFromViewOnPush) {
      super(removesFromViewOnPush);
   }

   public RevertHorizontalChangeHandler(long duration) {
      super(duration);
   }

   public RevertHorizontalChangeHandler(long duration, boolean removesFromViewOnPush) {
      super(duration, removesFromViewOnPush);
   }

   protected Animator getAnimator(@NonNull ViewGroup container, View from, View to, boolean isPush, boolean toAddedToContainer) {
      AnimatorSet animatorSet = new AnimatorSet();
      if (!isPush) {
         if (from != null) {
            animatorSet.play(ObjectAnimator.ofFloat(from, View.TRANSLATION_X, new float[]{(float) (-from.getWidth())}));
         }

         if (to != null) {
            animatorSet.play(ObjectAnimator.ofFloat(to, View.TRANSLATION_X, new float[]{(float) to.getWidth(), 0.0F}));
         }
      } else {
         if (from != null) {
            animatorSet.play(ObjectAnimator.ofFloat(from, View.TRANSLATION_X, new float[]{(float) from.getWidth()}));
         }

         if (to != null) {
            animatorSet.play(ObjectAnimator.ofFloat(to, View.TRANSLATION_X, new float[]{(float) (-to.getWidth()), 0.0F}));
         }
      }

      return animatorSet;
   }

   protected void resetFromView(@NonNull View from) {
      from.setTranslationX(0.0F);
   }
}