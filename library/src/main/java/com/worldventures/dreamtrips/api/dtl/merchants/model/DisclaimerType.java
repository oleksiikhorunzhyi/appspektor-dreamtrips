package com.worldventures.dreamtrips.api.dtl.merchants.model;

import com.google.gson.annotations.SerializedName;

public enum DisclaimerType {
    @SerializedName("points")POINTS,
    @SerializedName("perks")PERKS,
    @SerializedName("additional")ADDITIONAL,
    UNKNOWN
}
