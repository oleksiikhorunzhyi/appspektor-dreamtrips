package com.worldventures.dreamtrips.modules.dtl.helper.inflater;

import android.support.annotation.Nullable;
import android.view.View;

import com.worldventures.dreamtrips.modules.dtl.model.merchant.DtlMerchant;
import com.worldventures.dreamtrips.modules.dtl.model.merchant.offer.DtlOfferData;

import butterknife.ButterKnife;

public abstract class DtlMerchantDataInflater {

    View rootView;
    DtlOfferData expandedOffer;

    public void setView(View rootView) {
        this.rootView = rootView;
        ButterKnife.inject(this, rootView);
    }

    public void apply(DtlMerchant merchant, @Nullable DtlOfferData expandedOffer) {
        if (rootView == null) {
            throw new IllegalStateException("Root view is not set, call setView() method first");
        }
        this.expandedOffer = expandedOffer;
        onMerchantApply(merchant);
    }

    protected abstract void onMerchantApply(DtlMerchant merchant);
}
