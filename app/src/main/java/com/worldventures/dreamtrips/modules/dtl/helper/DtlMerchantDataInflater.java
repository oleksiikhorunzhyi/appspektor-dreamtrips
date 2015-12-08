package com.worldventures.dreamtrips.modules.dtl.helper;

import android.view.View;

import com.worldventures.dreamtrips.modules.dtl.model.merchant.DtlMerchant;

import butterknife.ButterKnife;

public abstract class DtlMerchantDataInflater {

    View rootView;

    public void setView(View rootView) {
        this.rootView = rootView;
        ButterKnife.inject(this, rootView);
    }

    public void apply(DtlMerchant merchant) {
        if (rootView == null) {
            throw new IllegalStateException("Root view is not set, call setView() method first");
        }
        onMerchantApply(merchant);
    }

    protected abstract void onMerchantApply(DtlMerchant merchant);
}
