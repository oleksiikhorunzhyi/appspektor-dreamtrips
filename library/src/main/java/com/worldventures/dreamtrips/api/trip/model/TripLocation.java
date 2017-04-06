package com.worldventures.dreamtrips.api.trip.model;

import com.google.gson.annotations.SerializedName;

import org.immutables.gson.Gson;
import org.immutables.value.Value;

@Gson.TypeAdapters
@Value.Immutable
public interface TripLocation {

    @SerializedName("name")
    String name();
    @SerializedName("lat")
    Double lat();
    @SerializedName("lng")
    Double lng();

}

