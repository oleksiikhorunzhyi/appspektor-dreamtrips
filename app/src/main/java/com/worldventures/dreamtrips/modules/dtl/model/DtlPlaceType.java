package com.worldventures.dreamtrips.modules.dtl.model;

import android.support.annotation.StringRes;

import com.worldventures.dreamtrips.R;

public enum DtlPlaceType {
    OFFER(R.string.dtl_place_tab_offers),
    DINING(R.string.dtl_place_tab_dining);

    @StringRes
    protected int typedListCaptionResId;

    DtlPlaceType(@StringRes int resId) {
        this.typedListCaptionResId = resId;
    }

    @StringRes
    public int getCaptionResId() {
        return typedListCaptionResId;
    }
}
