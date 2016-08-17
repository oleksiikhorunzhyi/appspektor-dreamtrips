package com.worldventures.dreamtrips.modules.dtl.helper.inflater;

import android.view.View;

import com.worldventures.dreamtrips.modules.dtl.model.merchant.DtlMerchant;

public interface MerchantInflater {

   void setView(View rootView);

   void applyMerchant(DtlMerchant merchant);

   void release();
}
