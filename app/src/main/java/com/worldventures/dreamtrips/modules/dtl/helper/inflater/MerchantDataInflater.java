package com.worldventures.dreamtrips.modules.dtl.helper.inflater;

import android.view.View;

import com.jakewharton.rxbinding.internal.Preconditions;
import com.worldventures.dreamtrips.modules.dtl.model.merchant.DtlMerchant;

import butterknife.ButterKnife;

public abstract class MerchantDataInflater implements MerchantInflater {

    protected View rootView;
    protected DtlMerchant merchant;

    @Override
    public void setView(View rootView) {
        Preconditions.checkNotNull(rootView, "View is null");
        this.rootView = rootView;
        ButterKnife.inject(this, rootView);
    }

    @Override
    public void applyMerchant(DtlMerchant merchant) {
        Preconditions.checkNotNull(rootView, "Root view is not set, call setView() method first");
        Preconditions.checkNotNull(merchant, "Merchant must be not null");
        this.merchant = merchant;
        onMerchantApply();
    }

    @Override
    public void release() {
        ButterKnife.reset(this);
        this.rootView = null;
    }

    protected abstract void onMerchantApply();
}
