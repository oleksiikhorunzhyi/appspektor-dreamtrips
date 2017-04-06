package com.worldventures.dreamtrips.api.trip.model;

import com.google.gson.annotations.SerializedName;

import org.immutables.gson.Gson;
import org.immutables.value.Value;

@Gson.TypeAdapters
@Value.Immutable
public interface TripPinCoordinates {
    @SerializedName("lat")
    double lat();
    @SerializedName("lng")
    double lng();
}
