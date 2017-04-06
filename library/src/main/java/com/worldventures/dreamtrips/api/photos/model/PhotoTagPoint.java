package com.worldventures.dreamtrips.api.photos.model;

import com.google.gson.annotations.SerializedName;

import org.immutables.gson.Gson;
import org.immutables.value.Value;

@Gson.TypeAdapters
@Value.Immutable
public interface PhotoTagPoint {
    @SerializedName("x")
    double x();
    @SerializedName("y")
    double y();
}
