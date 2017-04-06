package com.worldventures.dreamtrips.api.trip.model;

import com.google.gson.annotations.SerializedName;

import org.immutables.gson.Gson;
import org.immutables.value.Value;

import java.util.List;

@Gson.TypeAdapters
@Value.Immutable
public interface TripPin {
    @SerializedName("coordinates")
    TripPinCoordinates coordinates();
    @SerializedName("trip_uids")
    List<String> tripsUids();
}
