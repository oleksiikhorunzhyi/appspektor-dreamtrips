package com.worldventures.dreamtrips.modules.dtl.model.merchant;

import android.support.annotation.StringRes;

import com.worldventures.dreamtrips.R;

public enum DtlMerchantType {
   OFFER(R.string.dtl_merchant_tab_offers),
   DINING(R.string.dtl_merchant_tab_dining);

   @StringRes protected int typedListCaptionResId;

   DtlMerchantType(@StringRes int resId) {
      this.typedListCaptionResId = resId;
   }

   @StringRes
   public int getCaptionResId() {
      return typedListCaptionResId;
   }
}
