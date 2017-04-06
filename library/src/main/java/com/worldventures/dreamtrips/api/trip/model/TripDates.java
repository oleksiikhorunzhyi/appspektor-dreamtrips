package com.worldventures.dreamtrips.api.trip.model;

import com.google.gson.annotations.SerializedName;

import org.immutables.gson.Gson;
import org.immutables.value.Value;

import java.util.Date;

@Gson.TypeAdapters
@Value.Immutable
public interface TripDates {
    @SerializedName("start_on")
    Date startOn();
    @SerializedName("end_on")
    Date endOn();
}
