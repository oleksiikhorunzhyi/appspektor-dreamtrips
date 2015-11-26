package com.worldventures.dreamtrips.modules.dtl.model;

import android.support.annotation.StringRes;

import com.google.gson.annotations.SerializedName;
import com.worldventures.dreamtrips.R;

public enum DtlPlaceType {
    @SerializedName("offer")
    OFFER(R.string.dtl_place_tab_offers),
    @SerializedName("dinning")
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
