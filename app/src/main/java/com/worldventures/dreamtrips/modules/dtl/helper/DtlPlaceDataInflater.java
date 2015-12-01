package com.worldventures.dreamtrips.modules.dtl.helper;

import android.view.View;

import com.worldventures.dreamtrips.modules.dtl.model.DTlMerchant;

import butterknife.ButterKnife;

public abstract class DtlPlaceDataInflater {

    View rootView;

    public void setView(View rootView) {
        this.rootView = rootView;
        ButterKnife.inject(this, rootView);
    }

    public void apply(DTlMerchant place) {
        if (rootView == null) {
            throw new IllegalStateException("Root view is not set, call setView() method first");
        }
        onPlaceApply(place);
    }

    protected abstract void onPlaceApply(DTlMerchant place);
}
