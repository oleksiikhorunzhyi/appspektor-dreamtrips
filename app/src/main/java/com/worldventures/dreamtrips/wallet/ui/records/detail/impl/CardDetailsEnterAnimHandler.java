package com.worldventures.dreamtrips.wallet.ui.records.detail.impl;

import android.animation.Animator;
import android.animation.AnimatorSet;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.view.View;
import android.view.ViewGroup;

import com.bluelinelabs.conductor.changehandler.AnimatorChangeHandler;
import com.worldventures.dreamtrips.R;
import com.worldventures.dreamtrips.wallet.ui.dashboard.util.model.TransitionModel;

import static android.animation.ObjectAnimator.ofFloat;

public class CardDetailsEnterAnimHandler extends AnimatorChangeHandler {

   private static final String KEY_TRANSITION_MODEL = "key_transition_model";

   private static final long CARD_TRANSITION_DURATION = 350;
   private TransitionModel transitionModel;

   public CardDetailsEnterAnimHandler() {
      super(CARD_TRANSITION_DURATION);
   }

   public CardDetailsEnterAnimHandler(TransitionModel transitionModel) {
      super(CARD_TRANSITION_DURATION);
      this.transitionModel = transitionModel;
   }

   @Override
   protected Animator getAnimator(@NonNull ViewGroup container, View from, View to, boolean isPush, boolean toAddedToContainer) {
      final View controlsLayout = to.findViewById(R.id.controls_layout);
      final View cardView = to.findViewById(R.id.card);
      final float y = calcTargetTranslationY(transitionModel, cardView);

      cardView.setVisibility(View.VISIBLE);
      controlsLayout.setAlpha(0);
      cardView.setTranslationY(y);

      final AnimatorSet animatorSet = new AnimatorSet();
      animatorSet.play(ofFloat(cardView, View.TRANSLATION_Y, y, 0))
            .before(ofFloat(controlsLayout, View.ALPHA, 0, 1))
            .with(ofFloat(from, View.ALPHA, 1, 0).setDuration(50));
      return animatorSet;
   }

   @Override
   protected void resetFromView(@NonNull View from) {
   }

   @Override
   public void saveToBundle(@NonNull Bundle bundle) {
      super.saveToBundle(bundle);
      bundle.putParcelable(KEY_TRANSITION_MODEL, transitionModel);
   }

   @Override
   public void restoreFromBundle(@NonNull Bundle bundle) {
      super.restoreFromBundle(bundle);
      transitionModel = bundle.getParcelable(KEY_TRANSITION_MODEL);
   }

   private float calcTargetTranslationY(TransitionModel params, View view) {
      int[] coords = new int[2];
      view.getLocationOnScreen(coords);
      return params.getTop() - coords[1] + params.getOverlap();
   }


}
