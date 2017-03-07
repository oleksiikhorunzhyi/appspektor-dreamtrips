package com.worldventures.dreamtrips.wallet.ui.records.swiping.anim;


import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.view.View;
import android.view.animation.Animation;

import com.trello.rxlifecycle.RxLifecycle;
import com.worldventures.dreamtrips.core.utils.ViewUtils;

import java.util.concurrent.TimeUnit;

import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;

public class ChargingSwipingAnimations {

   public final static int BANKCARD_ANIMATION_REPEAT_DEFAULT = 3;

   private final static int SMARTCARD_TRANSLATION_DISTANCE_DP = 120;
   private final static int SMARTCARD_TRANSLATION_DISTANCE_FINISH_DP = 100;
   private final static int SMARTCARD_ANIMATION_DURATION = 800;

   private final static int BANKCARD_TRANSLATION_DISTANCE__DP = 200;
   private final static int BANKCARD_DELAY_BETWEEN_ANIMATIONS = 1200;
   private final static int BANKCARD_ANIMATION_DURATION = 1600;
   private final static int BANKCARD_ANIMATION_DELAY = 1000;

   public void animateSmartCard(View card) {
      ViewUtils.runTaskAfterMeasure(card, () -> {
         float distance = ViewUtils.pxFromDp(card.getContext(), SMARTCARD_TRANSLATION_DISTANCE_DP);
         AnimatorSet way = new AnimatorSet();
         ObjectAnimator translation = ObjectAnimator.ofFloat(card, View.X, distance + card.getLeft(), card
               .getLeft() - SMARTCARD_TRANSLATION_DISTANCE_FINISH_DP)
               .setDuration(SMARTCARD_ANIMATION_DURATION);
         ObjectAnimator alpha = ObjectAnimator.ofFloat(card, View.ALPHA, 0f, 1f)
               .setDuration(SMARTCARD_ANIMATION_DURATION / 2);
         way.playTogether(translation, alpha);
         way.start();
      });
   }

   public void animateBankCard(View card, int repeatCount) {
      ViewUtils.runTaskAfterMeasure(card, () -> {
         float distance = ViewUtils.pxFromDp(card.getContext(), BANKCARD_TRANSLATION_DISTANCE__DP);
         float halfWayDistance = distance / 2;
         int halfWayDuration = BANKCARD_ANIMATION_DURATION / 2;
         int halfWayAlphaAnimDuration = halfWayDuration / 2;

         AnimatorSet halfWay1 = new AnimatorSet();
         ObjectAnimator translation1 = ObjectAnimator.ofFloat(card, View.Y, card.getTop() - halfWayDistance, card.getTop())
               .setDuration(halfWayDuration);
         ObjectAnimator alpha1 = ObjectAnimator.ofFloat(card, View.ALPHA, 0f, 1f).setDuration(halfWayAlphaAnimDuration);
         halfWay1.playTogether(translation1, alpha1);

         AnimatorSet halfWay2 = new AnimatorSet();
         ObjectAnimator translation2 = ObjectAnimator.ofFloat(card, View.Y, card.getTop(), card.getTop() + halfWayDistance)
               .setDuration(halfWayDuration);
         ObjectAnimator alpha2 = ObjectAnimator.ofFloat(card, View.ALPHA, 1f, 0f).setDuration(halfWayAlphaAnimDuration);
         alpha2.setStartDelay(halfWayAlphaAnimDuration);
         halfWay2.playTogether(translation2, alpha2);

         AnimatorSet fullWay = new AnimatorSet();
         fullWay.playSequentially(halfWay1, halfWay2);
         fullWay.setStartDelay(BANKCARD_ANIMATION_DELAY);
         fullWay.addListener(new BankCardAnimListener(card, repeatCount - 1));
         fullWay.start();
      });
   }

   private class BankCardAnimListener extends AnimatorListenerAdapter {

      private final View animatedView;
      private final int remainRepeat;

      public BankCardAnimListener(View view, int remainRepeat) {
         this.animatedView = view;
         this.remainRepeat = remainRepeat;
      }

      @Override
      public void onAnimationEnd(Animator animator) {
         animator.removeAllListeners();
         Observable.just(remainRepeat)
               .filter(repeatCounter -> repeatCounter > 0 || repeatCounter < Animation.INFINITE)
               .delay(BANKCARD_DELAY_BETWEEN_ANIMATIONS, TimeUnit.MILLISECONDS, AndroidSchedulers.mainThread())
               .compose(RxLifecycle.bindView(animatedView))
               .subscribe(remainRepeat -> animateBankCard(animatedView, remainRepeat));
      }

   }

}
