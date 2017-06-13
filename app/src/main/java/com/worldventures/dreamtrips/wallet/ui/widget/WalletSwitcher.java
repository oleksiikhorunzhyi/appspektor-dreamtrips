package com.worldventures.dreamtrips.wallet.ui.widget;

import android.content.Context;
import android.support.v7.widget.SwitchCompat;
import android.util.AttributeSet;

public class WalletSwitcher extends SwitchCompat {

   private OnCheckedChangeListener listener;

   public WalletSwitcher(Context context) {
      super(context);
   }

   public WalletSwitcher(Context context, AttributeSet attrs) {
      super(context, attrs);
   }

   public WalletSwitcher(Context context, AttributeSet attrs, int defStyleAttr) {
      super(context, attrs, defStyleAttr);
   }

   @Override
   public void setOnCheckedChangeListener(OnCheckedChangeListener listener) {
      this.listener = listener;
      super.setOnCheckedChangeListener(listener);
   }

   public void setCheckedWithoutNotify(boolean checked) {
      super.setOnCheckedChangeListener(null);
      setChecked(checked);
      super.setOnCheckedChangeListener(listener);
   }
}
