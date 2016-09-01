package com.worldventures.dreamtrips.wallet.ui.widget.anim;

import android.animation.AnimatorInflater;
import android.animation.AnimatorSet;
import android.content.Context;
import android.view.View;

import com.badoo.mobile.util.WeakHandler;
import com.worldventures.dreamtrips.R;

public class FlipAnim {

   private AnimatorSet setRightOut;
   private AnimatorSet setLeftIn;
   private boolean isBackVisible = false;
   private View cardFrontLayout;
   private View cardBackLayout;

   private WeakHandler handler = new WeakHandler();

   private FlipAnim(View cardFrontLayout, View cardBackLayout) {
      this.cardFrontLayout = cardFrontLayout;
      this.cardBackLayout = cardBackLayout;
   }

   public void flipCard() {flipCard(0);}

   public void flipCard(long delay) {
      handler.postDelayed(() -> {
         if (!isBackVisible) {
            setRightOut.setTarget(cardFrontLayout);
            setLeftIn.setTarget(cardBackLayout);
            setRightOut.start();
            setLeftIn.start();
            isBackVisible = true;
         } else {
            setRightOut.setTarget(cardBackLayout);
            setLeftIn.setTarget(cardFrontLayout);
            setRightOut.start();
            setLeftIn.start();
            isBackVisible = false;
         }
      }, delay);
   }

   private void init(Context context) {
      setRightOut = (AnimatorSet) AnimatorInflater.loadAnimator(context, R.animator.wallet_smart_card_flip_out_animation);
      setLeftIn = (AnimatorSet) AnimatorInflater.loadAnimator(context, R.animator.wallet_smart_card_flip_in_animation);
   }

   public static class Builder {

      private View cardFrontLayout;
      private View cardBackLayout;

      public Builder setCardFrontLayout(View cardFrontLayout) {
         this.cardFrontLayout = cardFrontLayout;
         return this;
      }

      public Builder setCardBackLayout(View cardBackLayout) {
         this.cardBackLayout = cardBackLayout;
         return this;
      }

      public FlipAnim createAnim() {
         FlipAnim flipAnim = new FlipAnim(cardFrontLayout, cardBackLayout);
         flipAnim.init(cardFrontLayout.getContext());
         return flipAnim;
      }
   }
}
