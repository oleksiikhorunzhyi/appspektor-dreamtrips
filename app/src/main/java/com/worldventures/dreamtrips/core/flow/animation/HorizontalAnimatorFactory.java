package com.worldventures.dreamtrips.core.flow.animation;

import android.animation.Animator;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.view.View;
import android.view.ViewGroup;

public class HorizontalAnimatorFactory extends DirectionalAnimatorFactory {

   @Override
   protected Animator getForwardAnimator(View from, View to, ViewGroup container) {
      Animator animatorFrom = ObjectAnimator.ofFloat(from, View.TRANSLATION_X, -from.getWidth());
      Animator animatorTo = ObjectAnimator.ofFloat(to, View.TRANSLATION_X, to.getWidth(), 0);

      AnimatorSet set = new AnimatorSet();
      set.playTogether(animatorFrom, animatorTo);
      return set;
   }

   @Override
   protected Animator getBackwardAnimator(View from, View to, ViewGroup container) {
      Animator animatorFrom = ObjectAnimator.ofFloat(from, View.TRANSLATION_X, from.getWidth());
      Animator animatorTo = ObjectAnimator.ofFloat(to, View.TRANSLATION_X, -to.getWidth(), 0);

      AnimatorSet set = new AnimatorSet();
      set.playTogether(animatorFrom, animatorTo);
      return set;
   }

   @Override
   protected Animator getReplaceAnimator(View from, View to, ViewGroup container) {
      Animator animatorFrom = ObjectAnimator.ofFloat(from, View.ALPHA, 1F, 0F);
      Animator animatorTo = ObjectAnimator.ofFloat(to, View.ALPHA, 0F, 1F);

      AnimatorSet set = new AnimatorSet();
      set.playTogether(animatorFrom, animatorTo);
      return set;
   }
}
