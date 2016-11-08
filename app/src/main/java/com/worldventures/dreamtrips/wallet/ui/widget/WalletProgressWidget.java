package com.worldventures.dreamtrips.wallet.ui.widget;


import android.content.Context;
import android.util.AttributeSet;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;

import com.worldventures.dreamtrips.R;

public class WalletProgressWidget extends ImageView {

   private static final int ANIM_DURATION_MILLIS = 1000;

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
   }

   public void stop() {
      this.clearAnimation();
   }
}
