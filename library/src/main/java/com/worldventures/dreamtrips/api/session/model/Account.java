package com.worldventures.dreamtrips.api.session.model;

import com.google.gson.annotations.SerializedName;
import com.worldventures.dreamtrips.api.profile.model.UserProfile;

import org.immutables.gson.Gson;
import org.immutables.value.Value;

@Gson.TypeAdapters
@Value.Immutable
public interface Account extends UserProfile {

    @SerializedName("dream_trips_points")
    double dreamTripsPoints();

    @SerializedName("rovia_bucks")
    double roviaBucks();

}
