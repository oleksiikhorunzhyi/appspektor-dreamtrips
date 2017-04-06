package com.worldventures.dreamtrips.api.trip.model;

import com.google.gson.annotations.SerializedName;

import org.jetbrains.annotations.Nullable;

import java.util.Date;
import java.util.List;

public interface TripParams {

    @Nullable
    @SerializedName("query")
    String query();
    @Nullable
    @SerializedName("duration_min")
    Integer durationMin();
    @Nullable
    @SerializedName("duration_max")
    Integer durationMax();
    @Nullable
    @SerializedName("price_min")
    Double priceMin();
    @Nullable
    @SerializedName("price_max")
    Double priceMax();
    @Nullable
    @SerializedName("start_date")
    Date startDate();
    @Nullable
    @SerializedName("end_date")
    Date endDate();
    @Nullable
    @SerializedName("regions")
    List<Integer> regions();
    @Nullable
    @SerializedName("activities")
    List<Integer> activities();
    @Nullable
    @SerializedName("sold_out")
    Boolean soldOut();
    @Nullable
    @SerializedName("recent")
    Boolean recentFirst();
    @Nullable
    @SerializedName("liked")
    Boolean liked();
}
