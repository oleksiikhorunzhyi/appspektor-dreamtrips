package com.worldventures.dreamtrips.modules.dtl.model;

import android.support.annotation.StringRes;

import com.worldventures.dreamtrips.R;

public enum DtlPlaceType {
    OFFER("offer", R.string.dtl_place_tab_offers),
    DINING("dining", R.string.dtl_place_tab_dining);

    protected String name;
    @StringRes
    protected int typedListCaptionResId;

    DtlPlaceType(String name, @StringRes int resId) {
        this.name = name;
        this.typedListCaptionResId = resId;
    }

    public String getName() {
        return name;
    }

    @StringRes
    public int getCaptionResId() {
        return typedListCaptionResId;
    }
}
