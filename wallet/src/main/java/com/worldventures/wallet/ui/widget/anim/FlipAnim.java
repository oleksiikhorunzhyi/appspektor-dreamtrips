package com.worldventures.wallet.ui.widget.anim;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.content.Context;
import android.os.Handler;
import android.view.View;

import com.worldventures.wallet.R;

import java.util.ArrayList;

import static android.animation.AnimatorInflater.loadAnimator;

public final class FlipAnim {

   private final static int FLIP_OUT_INDEX = 0;
   private final static int FLIP_IN_INDEX = 1;
   private final static int DISTANCE = 22000;

   private final AnimatorSet flipAnimation;
   private boolean isBackVisible = false;

   private final View cardFrontLayout;
   private final View cardBackLayout;

   private final Handler handler = new Handler();
   private final AnimatorListenerAdapter restartAnimationListener = new AnimatorListenerAdapter() {
      @Override
      public void onAnimationEnd(Animator animation) {
         super.onAnimationEnd(animation);
         restartFlipCard();
      }
   };

   private FlipAnim(View cardFrontLayout, View cardBackLayout) {
      this.cardFrontLayout = cardFrontLayout;
      this.cardBackLayout = cardBackLayout;
      Context context = cardBackLayout.getContext();

      AnimatorSet flipAnimation = new AnimatorSet();
      flipAnimation.playTogether(
            loadAnimator(context, R.animator.wallet_smart_card_flip_out_animation),
            loadAnimator(context, R.animator.wallet_smart_card_flip_in_animation));
      flipAnimation.addListener(restartAnimationListener);
      this.flipAnimation = flipAnimation;
   }

   private void restartFlipCard() {
      handler.post(this::startFlipping);
   }

   void flipCard(long delay) {
      handler.postDelayed(this::startFlipping, delay);
   }

   private void startFlipping() {
      if (!isBackVisible) {
         animateCard(cardFrontLayout, cardBackLayout);
         isBackVisible = true;
      } else {
         animateCard(cardBackLayout, cardFrontLayout);
         isBackVisible = false;
      }
   }

   private void animateCard(View front, View back) {
      final ArrayList<Animator> childAnimations = flipAnimation.getChildAnimations();
      childAnimations.get(FLIP_OUT_INDEX).setTarget(front);
      childAnimations.get(FLIP_IN_INDEX).setTarget(back);
      flipAnimation.start();
   }

   public void cancel() {
      handler.removeCallbacksAndMessages(null);
      flipAnimation.removeListener(restartAnimationListener);
      flipAnimation.cancel();
   }

   public static Builder builder() {
      return new Builder();
   }

   public static final class Builder {

      private View cardFrontLayout;
      private View cardBackLayout;

      private Builder() {
      }

      public Builder setCardFrontLayout(View cardFrontLayout) {
         this.cardFrontLayout = cardFrontLayout;
         return this;
      }

      public Builder setCardBackLayout(View cardBackLayout) {
         this.cardBackLayout = cardBackLayout;
         return this;
      }

      public FlipAnim flipCard(long delay) {
         final FlipAnim flipAnim = new FlipAnim(cardFrontLayout, cardBackLayout);
         final float scale = cardFrontLayout.getContext().getResources().getDisplayMetrics().density * DISTANCE;

         // default state
         cardBackLayout.setRotationY(0);
         cardBackLayout.setAlpha(0);
         cardFrontLayout.setRotation(0);
         cardFrontLayout.setAlpha(1);

         cardFrontLayout.setCameraDistance(scale);
         cardBackLayout.setCameraDistance(scale);
         flipAnim.flipCard(delay);
         return flipAnim;
      }
   }
}
