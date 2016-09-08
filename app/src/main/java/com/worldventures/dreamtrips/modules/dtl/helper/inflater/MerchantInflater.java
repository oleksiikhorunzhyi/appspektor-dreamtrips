package com.worldventures.dreamtrips.modules.dtl.helper.inflater;

import android.view.View;

import com.worldventures.dreamtrips.modules.dtl.model.merchant.DtlMerchant;
import com.worldventures.dreamtrips.modules.dtl.model.merchant.Merchant;

public interface MerchantInflater {

   void setView(View rootView);

   void applyMerchant(Merchant merchant);

   void release();
}
