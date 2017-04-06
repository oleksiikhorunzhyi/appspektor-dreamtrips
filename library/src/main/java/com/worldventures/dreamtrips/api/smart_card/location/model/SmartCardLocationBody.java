package com.worldventures.dreamtrips.api.smart_card.location.model;


import com.google.gson.annotations.SerializedName;

import org.immutables.gson.Gson;
import org.immutables.value.Value;

import java.util.List;

@Gson.TypeAdapters
@Value.Immutable
public interface SmartCardLocationBody {
    @SerializedName("locations")
    List<SmartCardLocation> locations();
}
