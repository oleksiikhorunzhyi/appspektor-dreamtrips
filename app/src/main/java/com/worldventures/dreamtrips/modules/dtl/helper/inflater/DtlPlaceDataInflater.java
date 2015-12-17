package com.worldventures.dreamtrips.modules.dtl.helper.inflater;

import android.view.View;

import com.worldventures.dreamtrips.modules.dtl.model.merchant.DtlMerchant;

import butterknife.ButterKnife;

public abstract class DtlPlaceDataInflater {

    View rootView;

    public void setView(View rootView) {
        this.rootView = rootView;
        ButterKnife.inject(this, rootView);
    }

    public void apply(DtlMerchant place) {
        if (rootView == null) {
            throw new IllegalStateException("Root view is not set, call setView() method first");
        }
        onPlaceApply(place);
    }

    protected abstract void onPlaceApply(DtlMerchant place);
}
