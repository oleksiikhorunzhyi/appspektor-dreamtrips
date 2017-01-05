package com.worldventures.dreamtrips.modules.feed.view.cell.uploading.preview;

import android.animation.Animator;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.view.View;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.LinearInterpolator;

import com.messenger.ui.anim.SimpleAnimatorListener;
import com.worldventures.dreamtrips.modules.background_uploading.model.PhotoAttachment;

import java.util.List;

public abstract class BasePhotoAttachmentPreviewView implements PhotoAttachmentPreviewView {

   private static final int PULSE_CYCLE_DURATION = 1000;
   private static final float FADE_OUT_ALPHA = 0.7f;

   protected Context context;
   protected View rootView;

   protected AnimatorSet pulseAnimationSet;
   protected Animator.AnimatorListener pulseListener = new SimpleAnimatorListener() {
      @Override
      public void onAnimationEnd(Animator animator) {
         if (pulseAnimationSet != null) pulseAnimationSet.start();
      }
   };

   public BasePhotoAttachmentPreviewView(Context context) {
      this.context = context;
   }

   @Override
   public void showPreview(List<PhotoAttachment> attachments, boolean animate) {
      if (animate) {
         if (pulseAnimationSet != null) return;
         showPulseAnimation();
      } else {
         if (pulseAnimationSet != null) {
            pulseAnimationSet.removeAllListeners();
            if (pulseAnimationSet.isStarted()) {
               pulseAnimationSet.cancel();
            }
         }
         pulseAnimationSet = null;
         rootView.setAlpha(1f);
      }
   }

   private void showPulseAnimation() {
      pulseAnimationSet = new AnimatorSet();
      pulseAnimationSet.setInterpolator(new LinearInterpolator());
      pulseAnimationSet.addListener(pulseListener);
      pulseAnimationSet.playSequentially(getAlphaAnimator(1f, FADE_OUT_ALPHA),
            getAlphaAnimator(FADE_OUT_ALPHA, 1f));
      pulseAnimationSet.start();
   }

   private ObjectAnimator getAlphaAnimator(float alphaStart, float alphaEnd) {
      ObjectAnimator animator = ObjectAnimator.ofFloat(rootView, "alpha", alphaStart, alphaEnd);
      animator.setDuration(PULSE_CYCLE_DURATION);
      animator.setInterpolator(new AccelerateInterpolator());
      return animator;
   }
}
