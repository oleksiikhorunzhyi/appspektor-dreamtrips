package com.worldventures.dreamtrips.api.dtl.locations.model;

import com.google.gson.annotations.SerializedName;
import com.worldventures.dreamtrips.api.api_common.model.Identifiable;

import org.immutables.gson.Gson;
import org.immutables.value.Value;
import org.jetbrains.annotations.Nullable;

import java.util.List;

@Gson.TypeAdapters
@Value.Immutable
public interface Location extends Identifiable<String> {

    @SerializedName("type")
    LocationType type();
    @SerializedName("short_name")
    String shortName();
    @SerializedName("long_name")
    String longName();
    @SerializedName("coordinates")
    @Nullable
    Coordinates coordinates();
    @SerializedName("located_in")
    @Nullable
    List<Location> locatedIn();
}
