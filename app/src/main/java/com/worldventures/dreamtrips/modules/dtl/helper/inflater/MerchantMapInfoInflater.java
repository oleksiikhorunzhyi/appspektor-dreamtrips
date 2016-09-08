package com.worldventures.dreamtrips.modules.dtl.helper.inflater;

import android.widget.TextView;

import com.worldventures.dreamtrips.R;
import com.worldventures.dreamtrips.core.utils.ViewUtils;

import butterknife.InjectView;

public class MerchantMapInfoInflater extends MerchantInfoInflater {

   @InjectView(R.id.merchant_title) TextView merchantTitle;

   @Override
   protected void onMerchantApply() {
      super.onMerchantApply();
      ViewUtils.setTextOrHideView(merchantTitle, merchant.displayName());
   }
}
