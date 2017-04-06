package com.worldventures.dreamtrips.api.trip.model;

import com.google.gson.annotations.SerializedName;
import com.worldventures.dreamtrips.api.api_common.model.HasLanguage;

import org.immutables.gson.Gson;
import org.immutables.value.Value;

import java.util.List;

@Gson.TypeAdapters
@Value.Immutable
public interface TripContent extends HasLanguage {

    @SerializedName("name")
    String name();
    @SerializedName("description")
    String description();
    @SerializedName("tags")
    List<String> tags();

}