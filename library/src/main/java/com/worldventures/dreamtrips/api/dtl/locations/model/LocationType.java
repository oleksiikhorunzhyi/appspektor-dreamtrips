package com.worldventures.dreamtrips.api.dtl.locations.model;


import com.google.gson.annotations.SerializedName;

public enum LocationType {

    @SerializedName("city")CITY,
    @SerializedName("metro")METRO,
    @SerializedName("state")STATE,
    @SerializedName("country")COUNTRY,
    UNKNOWN
}
