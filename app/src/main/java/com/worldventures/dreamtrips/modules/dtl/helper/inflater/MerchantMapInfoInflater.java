package com.worldventures.dreamtrips.modules.dtl.helper.inflater;

import android.widget.TextView;

import com.worldventures.core.janet.Injector;
import com.worldventures.core.ui.util.ViewUtils;
import com.worldventures.dreamtrips.R;

import butterknife.InjectView;

public class MerchantMapInfoInflater extends MerchantInfoInflater {

   @InjectView(R.id.merchant_title) TextView merchantTitle;

   public MerchantMapInfoInflater(Injector injector) {
      super(injector);
   }

   @Override
   protected void onMerchantAttributesApply() {
      super.onMerchantAttributesApply();
      ViewUtils.setTextOrHideView(merchantTitle, merchantAttributes.displayName());
   }
}
