package com.worldventures.dreamtrips.modules.dtl.helper.inflater;

import android.view.View;

public interface MerchantInflater {

   void setView(View rootView);

   void applyMerchantAttributes(MerchantAttributes merchant);

   void release();
}
