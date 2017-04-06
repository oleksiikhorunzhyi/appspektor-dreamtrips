package com.worldventures.dreamtrips.api.dtl.locations.model;

import com.google.gson.annotations.SerializedName;

import org.immutables.gson.Gson;
import org.immutables.value.Value;

@Gson.TypeAdapters
@Value.Immutable
public interface Coordinates {

    @SerializedName("lat")
    Double lat();
    @SerializedName("lng")
    Double lng();
}
