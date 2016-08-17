package com.worldventures.dreamtrips.modules.dtl_flow.animation;

import android.animation.Animator;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.DecelerateInterpolator;

import com.worldventures.dreamtrips.core.flow.animation.DirectionalAnimatorFactory;

public class FadeAnimatorFactory extends DirectionalAnimatorFactory {

   @Override
   protected Animator getForwardAnimator(View from, View to, ViewGroup container) {
      Animator animatorToScaleX = ObjectAnimator.ofFloat(to, View.SCALE_X, 1.5F, 1);
      animatorToScaleX.setInterpolator(new DecelerateInterpolator());

      Animator animatorToScaleY = ObjectAnimator.ofFloat(to, View.SCALE_Y, 1.5F, 1);
      animatorToScaleY.setInterpolator(new DecelerateInterpolator());

      Animator animatorToAlpha = ObjectAnimator.ofFloat(to, View.ALPHA, 0F, 1F);
      animatorToAlpha.setInterpolator(new AccelerateInterpolator());

      AnimatorSet set = new AnimatorSet();
      set.playTogether(animatorToScaleX, animatorToScaleY, animatorToAlpha);
      return set;
   }

   @Override
   protected Animator getBackwardAnimator(View from, View to, ViewGroup container) {
      Animator animatorFromScaleX = ObjectAnimator.ofFloat(from, View.SCALE_X, 1F, 1.5F);
      animatorFromScaleX.setInterpolator(new AccelerateInterpolator());

      Animator animatorFromScaleY = ObjectAnimator.ofFloat(from, View.SCALE_Y, 1F, 1.5F);
      animatorFromScaleY.setInterpolator(new AccelerateInterpolator());

      Animator animatorFromAlpha = ObjectAnimator.ofFloat(from, View.ALPHA, 1F, 0F);
      animatorFromAlpha.setInterpolator(new DecelerateInterpolator());

      Animator toAlpha = ObjectAnimator.ofFloat(to, View.ALPHA, 0F, 1F);

      AnimatorSet set = new AnimatorSet();
      set.playTogether(animatorFromScaleX, animatorFromScaleY, animatorFromAlpha, toAlpha);
      return set;
   }

   @Override
   protected Animator getReplaceAnimator(View from, View to, ViewGroup container) {
      return getForwardAnimator(from, to, container);
   }
}
