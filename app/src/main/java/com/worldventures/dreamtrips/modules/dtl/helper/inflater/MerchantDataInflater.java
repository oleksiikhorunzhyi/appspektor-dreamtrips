package com.worldventures.dreamtrips.modules.dtl.helper.inflater;

import android.view.View;

import com.jakewharton.rxbinding.internal.Preconditions;

import butterknife.ButterKnife;

public abstract class MerchantDataInflater implements MerchantInflater {

   protected View rootView;
   protected MerchantAttributes merchantAttributes;

   @Override
   public void setView(View rootView) {
      Preconditions.checkNotNull(rootView, "View is null");
      this.rootView = rootView;
      ButterKnife.inject(this, rootView);
   }

   @Override
   public void applyMerchantAttributes(MerchantAttributes merchantAttributes) {
      Preconditions.checkNotNull(rootView, "Root view is not set, call setView() method first");
      this.merchantAttributes = merchantAttributes;
      onMerchantAttributesApply();
   }

   @Override
   public void release() {
      ButterKnife.reset(this);
      this.rootView = null;
   }

   protected abstract void onMerchantAttributesApply();
}
