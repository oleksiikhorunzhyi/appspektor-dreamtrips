package com.worldventures.wallet.ui.widget;


import android.content.Context;
import android.support.v7.widget.AppCompatImageView;
import android.util.AttributeSet;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;

import com.worldventures.wallet.R;

public class WalletProgressWidget extends AppCompatImageView {

   private static final int ANIM_DURATION_MILLIS = 1000;

   private boolean isStarted = false;

   public WalletProgressWidget(Context context) {
      super(context);
   }

   public WalletProgressWidget(Context context, AttributeSet attrs) {
      super(context, attrs);
   }

   public WalletProgressWidget(Context context, AttributeSet attrs, int defStyleAttr) {
      super(context, attrs, defStyleAttr);
   }

   public void start() {
      Animation anim = AnimationUtils.loadAnimation(getContext(), R.anim.wallet_progress_anim);
      anim.setDuration(ANIM_DURATION_MILLIS);
      this.startAnimation(anim);
      isStarted = true;
   }

   public void stop() {
      this.clearAnimation();
      isStarted = false;
   }

   public boolean isStarted() {
      return isStarted;
   }
}
