package com.worldventures.dreamtrips.wallet.ui.wizard.input.scaner;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.view.View;
import android.view.ViewGroup;

import com.worldventures.dreamtrips.core.flow.animation.DirectionalAnimatorFactory;

import static android.animation.ObjectAnimator.ofFloat;

public class WizardScannerEnterAnimFactory extends DirectionalAnimatorFactory {

   @Override
   protected Animator getForwardAnimator(View from, View to, ViewGroup container) {
      final WizardScanBarcodeScreen toScreen = (WizardScanBarcodeScreen) to;
      final Animator animator = createAnimator(toScreen, from, container.getHeight() * 0.66f, 0, .5F, 1F, 0F, 1F, 1F, 0F);
      animator.addListener(new AnimatorListenerAdapter() {
         @Override
         public void onAnimationEnd(Animator animation) {
            toScreen.onPostEnterAnimation();
         }
      });
      return animator;
   }

   @Override
   protected Animator getBackwardAnimator(View from, View to, ViewGroup container) {
      final WizardScanBarcodeScreen fromScreen = (WizardScanBarcodeScreen) from;
      fromScreen.onPreExitAnimation();
      return createAnimator(fromScreen, to, 0, container.getHeight() * 0.66F, 1F, .5F, 1F, 0F, 0F, 1F);
   }

   @Override
   protected Animator getReplaceAnimator(View from, View to, ViewGroup container) {
      return getForwardAnimator(from, to, container);
   }

   private Animator createAnimator(WizardScanBarcodeScreen screen, View secondaryView,
         float translationYFrom, float translationYTo, float scaleFrom, float scaleTo,
         float alphaFrom, float alphaTo, float secondaryAlphaFrom, float secondaryAlphaTo) {

      final AnimatorSet animatorSetTo = new AnimatorSet();
      final View contentView = screen.contentView;
      animatorSetTo.playTogether(
            ofFloat(contentView, View.TRANSLATION_Y, translationYFrom, translationYTo),
            ofFloat(contentView, View.SCALE_X, scaleFrom, scaleTo),
            ofFloat(contentView, View.ALPHA, alphaFrom, alphaTo));
      animatorSetTo.setDuration(320);

      final AnimatorSet set = new AnimatorSet();
      set.playTogether(ofFloat(secondaryView, View.ALPHA, secondaryAlphaFrom, secondaryAlphaTo).setDuration(300), animatorSetTo);
      return set;
   }
}
