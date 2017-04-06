package com.worldventures.dreamtrips.api.photos.model;

import com.google.gson.annotations.SerializedName;

import org.immutables.gson.Gson;
import org.immutables.value.Value;

@Gson.TypeAdapters
@Value.Immutable
public interface PhotoTagPosition {
    @SerializedName("top_left")
    PhotoTagPoint topLeft();
    @SerializedName("bottom_right")
    PhotoTagPoint bottomRight();
}
