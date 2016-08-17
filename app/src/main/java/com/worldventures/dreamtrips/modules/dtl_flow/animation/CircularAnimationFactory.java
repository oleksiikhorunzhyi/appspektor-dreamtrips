package com.worldventures.dreamtrips.modules.dtl_flow.animation;

import android.animation.Animator;
import android.view.View;
import android.view.ViewAnimationUtils;
import android.view.ViewGroup;

import com.worldventures.dreamtrips.core.flow.animation.AnimatorFactory;

import flow.Flow;

public class CircularAnimationFactory implements AnimatorFactory {

   // TODO :: 4/8/16 add ability to set up animation center based on some touch coordinates etc

   @Override
   public Animator createAnimator(View from, View to, Flow.Direction direction, ViewGroup container) {
      switch (direction) {
         case BACKWARD:
            return createBackwardAnimator(from, to);
         case REPLACE: // TODO :: 4/8/16 add animation for REPLACE
         case FORWARD:
         default:
            return createForwardAnimator(from, to);
      }
   }

   private Animator createForwardAnimator(View from, View to) {
      return ViewAnimationUtils.createCircularReveal(to, from.getWidth() / 2, from.getHeight() / 2, 0F, Math.max(to.getWidth(), to
            .getHeight()));
   }

   private Animator createBackwardAnimator(View from, View to) {
      return ViewAnimationUtils.createCircularReveal(from, from.getWidth() / 2, from.getHeight() / 2, Math.max(to.getWidth(), to
            .getHeight()), 0F);
   }
}
