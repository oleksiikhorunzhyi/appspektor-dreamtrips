package com.worldventures.dreamtrips.api.trip.model;

import com.google.gson.annotations.SerializedName;
import com.worldventures.dreamtrips.api.api_common.model.Identifiable;
import com.worldventures.dreamtrips.api.api_common.model.UniqueIdentifiable;
import com.worldventures.dreamtrips.api.likes.model.Likeable;

import org.jetbrains.annotations.Nullable;

import java.util.Date;
import java.util.List;
import java.util.Map;

public abstract class Trip implements Identifiable<Integer>, UniqueIdentifiable, Likeable {

    @SerializedName("trip_id")
    public abstract String tripId();

    @SerializedName("name")
    public abstract String name();
    @SerializedName("description")
    public abstract String description();
    @SerializedName("images")
    public abstract List<TripImage> images();

    @SerializedName("available")
    public abstract boolean available();
    @SerializedName("featured")
    public abstract boolean featured();
    @SerializedName("platinum")
    public abstract boolean platinum();

    @SerializedName("price")
    public abstract TripPrice price();
    @SerializedName("sold_out")
    public abstract boolean soldOut();

    @SerializedName("rewarded")
    public abstract boolean rewarded();
    @SerializedName("rewards_limit")
    public abstract long rewardsLimit();
    @SerializedName("rewards_rules")
    public abstract Map<TripReward, Integer> rewardRules();

    @SerializedName("location")
    public abstract TripLocation location();
    @Nullable
    @SerializedName("region")
    public abstract TripRegion region();
    @SerializedName("activities")
    public abstract List<TripActivity> activities();

    @SerializedName("duration")
    public abstract int duration();
    @SerializedName("dates")
    public abstract TripDates dates();
    @SerializedName("has_multiple_dates")
    public abstract boolean hasMultipleDates();

    @SerializedName("recent")
    public abstract boolean recentlyAdded();
    @SerializedName("in_bucket_list")
    public abstract boolean inBucketList();

    @SerializedName("created_at")
    public abstract Date createdAt();
    @SerializedName("updated_at")
    public abstract Date updatedAt();
    @SerializedName("location_id")
    public abstract int locationId();
    @SerializedName("regions")
    public abstract List<TripRegion> regions();
}
