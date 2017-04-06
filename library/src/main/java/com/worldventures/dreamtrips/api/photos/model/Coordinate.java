package com.worldventures.dreamtrips.api.photos.model;

import com.google.gson.annotations.SerializedName;

import org.immutables.gson.Gson;
import org.immutables.value.Value;
import org.jetbrains.annotations.Nullable;

@Gson.TypeAdapters
@Value.Immutable
public interface Coordinate {
    @SerializedName("lat")
    @Nullable
    Double lat();

    @SerializedName("lng")
    @Nullable
    Double lng();

}
