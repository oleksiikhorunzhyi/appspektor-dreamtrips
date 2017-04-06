package com.worldventures.dreamtrips.api.bucketlist.model;

import com.google.gson.annotations.SerializedName;

public enum BucketStatus {
    @SerializedName("new")NEW,
    @SerializedName("completed")COMPLETED,
    UNKNOWN;

    @Override
    public String toString() {
        return super.toString().toLowerCase();
    }
}
