package com.worldventures.dreamtrips.api.bucketlist.model;

import com.google.gson.annotations.SerializedName;

import org.immutables.gson.Gson;
import org.immutables.value.Value;

@Gson.TypeAdapters
@Value.Immutable
public interface BucketPhotoLocation {
    @SerializedName("lat")
    String lat();
    @SerializedName("lng")
    String lng();
}
