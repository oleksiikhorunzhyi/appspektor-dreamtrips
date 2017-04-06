package com.worldventures.dreamtrips.api.bucketlist.model;

import com.google.gson.annotations.SerializedName;

public enum BucketType {
    @SerializedName("Activity")ACTIVITY,
    @SerializedName("Dining")DINING,
    @SerializedName("Location")LOCATION,
    UNKNOWN;
}
