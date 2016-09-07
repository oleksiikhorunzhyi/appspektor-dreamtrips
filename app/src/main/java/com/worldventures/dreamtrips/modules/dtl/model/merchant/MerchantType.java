package com.worldventures.dreamtrips.modules.dtl.model.merchant;

import android.support.annotation.StringRes;

import com.worldventures.dreamtrips.R;

public enum MerchantType {
   OFFER(R.string.dtl_merchant_tab_offers),
   DINING(R.string.dtl_merchant_tab_dining);

   @StringRes protected int typedListCaptionResId;

   MerchantType(@StringRes int resId) {
      this.typedListCaptionResId = resId;
   }
}
