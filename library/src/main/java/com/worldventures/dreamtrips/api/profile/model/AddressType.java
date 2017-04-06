package com.worldventures.dreamtrips.api.profile.model;

import com.google.gson.annotations.SerializedName;

public enum AddressType {
    @SerializedName("billing")
    BILLING,
    @SerializedName("shipping")
    SHIPPING,
    @SerializedName("mailing")
    MAILING,

    UNKNOWN
}
