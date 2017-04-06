package com.worldventures.dreamtrips.api.dtl.attributes.model;

import com.google.gson.annotations.SerializedName;

public enum AttributeType {
    @SerializedName("AMENITY") AMENITY,
    @SerializedName("CATEGORY") CATEGORY,
    @SerializedName("AWARD") AWARD,
    UNKNOWN
}
