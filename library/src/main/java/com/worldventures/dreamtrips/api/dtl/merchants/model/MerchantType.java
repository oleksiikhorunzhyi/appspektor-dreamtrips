package com.worldventures.dreamtrips.api.dtl.merchants.model;

import com.google.gson.annotations.SerializedName;

public enum MerchantType {
    @SerializedName("restaurant")RESTAURANT,
    @SerializedName("bar")BAR,
    UNKNOWN
}
