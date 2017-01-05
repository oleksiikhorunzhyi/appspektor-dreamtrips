package com.worldventures.dreamtrips.modules.dtl.helper.inflater;

import android.widget.TextView;

import com.techery.spares.module.Injector;
import com.worldventures.dreamtrips.R;
import com.worldventures.dreamtrips.core.utils.ViewUtils;

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
