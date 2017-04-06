package com.worldventures.dreamtrips.api.trip.model;

import com.google.gson.annotations.SerializedName;
import com.worldventures.dreamtrips.api.api_common.model.Identifiable;

import org.immutables.gson.Gson;
import org.immutables.value.Value;
import org.jetbrains.annotations.Nullable;

@Gson.TypeAdapters
@Value.Immutable
public interface TripActivity extends Identifiable<Integer> {
    @SerializedName("name")
    String name();
    @SerializedName("parent_id")
    int parentId();
    @SerializedName("position")
    int position();
    @Nullable
    @SerializedName("icon")
    String icon();
}
