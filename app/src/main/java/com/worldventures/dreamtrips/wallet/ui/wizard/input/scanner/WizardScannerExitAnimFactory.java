package com.worldventures.dreamtrips.wallet.ui.wizard.input.scanner;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.view.View;
import android.view.ViewGroup;

import com.worldventures.dreamtrips.core.flow.animation.HorizontalAnimatorFactory;

public class WizardScannerExitAnimFactory extends HorizontalAnimatorFactory {

   @Override
   protected Animator getForwardAnimator(View from, View to, ViewGroup container) {
      ((WizardScanBarcodeScreen) from).onPreExitAnimation();
      return super.getForwardAnimator(from, to, container);
   }

   @Override
   protected Animator getBackwardAnimator(View from, View to, ViewGroup container) {
      final Animator animator = super.getBackwardAnimator(from, to, container);
      animator.addListener(new AnimatorListenerAdapter() {
         @Override
         public void onAnimationEnd(Animator animation) {
            ((WizardScanBarcodeScreen) to).onPostEnterAnimation();
         }
      });
      return animator;
   }

   @Override
   protected Animator getReplaceAnimator(View from, View to, ViewGroup container) {
      ((WizardScanBarcodeScreen) from).onPreExitAnimation();
      return super.getReplaceAnimator(from, to, container);
   }
}
