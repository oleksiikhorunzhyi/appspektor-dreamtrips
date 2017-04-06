package com.worldventures.dreamtrips.api.trip.model;

import com.google.gson.annotations.SerializedName;

import org.immutables.gson.Gson;
import org.immutables.value.Value;

@Gson.TypeAdapters
@Value.Immutable
public interface TripPrice {
    @SerializedName("amount")
    double amount();
    @SerializedName("currency")
    String currency();
}
